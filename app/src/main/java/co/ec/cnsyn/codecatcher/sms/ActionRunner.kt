package co.ec.cnsyn.codecatcher.sms

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import co.ec.cnsyn.codecatcher.App
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.code.Code
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithRegex
import co.ec.cnsyn.codecatcher.helpers.AppLogger
import co.ec.cnsyn.codecatcher.helpers.async
import co.ec.cnsyn.codecatcher.sms.actions.BaseAction
import co.ec.cnsyn.codecatcher.sms.actions.ClipboardAction
import co.ec.cnsyn.codecatcher.sms.actions.NotificationAction
import co.ec.cnsyn.codecatcher.sms.actions.SmsAction
import co.ec.cnsyn.codecatcher.sms.actions.TTSAction
import co.ec.cnsyn.codecatcher.helpers.EventBus
import co.ec.cnsyn.codecatcher.helpers.Settings
import co.ec.cnsyn.codecatcher.helpers.SmsCaught
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.logging.Logger

class ActionRunner {


    companion object {
        fun getActionInstance(className: String): BaseAction? {
            return when (className) {
                "NotificationAction" -> NotificationAction()
                "SmsAction" -> SmsAction()
                "TTSAction" -> TTSAction()
                "ClipboardAction" -> ClipboardAction()
                else -> null
            }
        }
    }

    fun runSmsList(
        messages: List<SmsData>,
        match: (sms: SmsData) -> Unit = { _ -> },
        then: () -> Unit = {}
    ) {
        //load all searchers
        async({ DB.get().catcher().getActiveCatchersWithRegexes() }, { catchers ->
            val catcherList=catchers.sortedByDescending { it.regex.regex.length }
            messages.forEach { sms ->
                testCatchers(sms, catcherList, match)
            }
            then()
        })
    }


    /**
     * test sms for searchers
     */
    private fun testCatchers(
        sms: SmsData, catchers: List<CatcherWithRegex>,
        match: (sms: SmsData) -> Unit = { }
    ) {
        //filter for senders
        val filtered =
            catchers.filter { it.catcher.sender == "" || it.catcher.sender == sms.sender }
        var matched = false
        filtered
            .filter { it.catcher.sender != "" }
            .forEach {
                if (!matched) {
                    matched = runOneCatcher(sms, it, match)
                }
            }
        if (!matched) {
            filtered.filter { it.catcher.sender == "" }
                .forEach {
                    if (!matched) {
                        matched = runOneCatcher(sms, it, match)
                    }
                }
        }


    }

    private fun runOneCatcher(
        sms: SmsData, catcher: CatcherWithRegex,
        match: (sms: SmsData) -> Unit = { }
    ): Boolean {
        //make pattern
        val searchPattern = catcher.regex.regex.toPattern().toRegex()
        AppLogger.d("regex test ${catcher.regex.regex} run on ${sms.body} => ${searchPattern.containsMatchIn(sms.body)}")
        if (searchPattern.containsMatchIn(sms.body)) {
            match(sms)
            val code = try {
                searchPattern.findAll(sms.body).toList().first().value
            } catch (e: Error) {
                ""
            }
            catcher.catcher.catchCount++
            copyToClipboard(code)
            //record to database
            async({
                //update search count
                DB.get().catcher().update(catcher.catcher)
                //insert extracted code to database
                val inserted = DB.get().code().insert(
                    Code(
                        date = sms.date,
                        catcherId = catcher.catcher.id,
                        sender = sms.sender,
                        code = code,
                        sms = sms.body
                    )
                )
                return@async inserted
            }, {
                //after inserted run actions
                runActions(sms, catcher)
            }, {
                error(it)
            })
            return true
        }
        return false
    }


    /**
     * run actions for search
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun runActions(message: SmsData, catcher: CatcherWithRegex) {

        if (catcher.actions.isNotEmpty()) {
            //if there is a action
            catcher.actions
                .filter { it.status == 1 } //filter actives
                .map { Pair(it, getActionInstance(it.action)) } //try to get instance
                .filter { it.second != null } //filter instance items
                .map { pair ->
                    //generate action instance
                    return@map pair.second?.run(catcher, pair.first, message)
                }
                .isNotEmpty() //test returned
                .let {
                    if (it) {
                        GlobalScope.launch {
                            //wait and send
                            delay(2500)
                            EventBus.publish(SmsCaught(message))
                        }
                    }
                }

        }

    }


    /**
     * copy code to clipboard
     */
    private fun copyToClipboard(code: String) {
        val copySettings=Settings.get().getBoolean("copyAllCodes", false)
        if(copySettings){
            val clipboard = App.context()
                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("code-catcher", code)
            clipboard.setPrimaryClip(clip)
        }
    }

}