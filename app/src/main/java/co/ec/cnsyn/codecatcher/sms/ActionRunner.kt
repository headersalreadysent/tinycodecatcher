package co.ec.cnsyn.codecatcher.sms


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.webkit.CookieSyncManager.createInstance
import co.ec.cnsyn.codecatcher.App
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.code.Code
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithActions
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithRegex
import co.ec.cnsyn.codecatcher.helpers.async
import co.ec.cnsyn.codecatcher.sms.actions.BaseAction
import co.ec.cnsyn.codecatcher.sms.actions.ClipboardAction
import co.ec.cnsyn.codecatcher.sms.actions.NotificationAction
import co.ec.cnsyn.codecatcher.sms.actions.SmsAction
import co.ec.cnsyn.codecatcher.sms.actions.TTSAction

class ActionRunner {

    private var emulation: Boolean = false

    fun emulate(status: Boolean = true) {
        emulation = status
    }

    fun runSmsList(
        messages: List<SmsData>,
        match: (sms: SmsData) -> Unit = { _ -> },
        then: () -> Unit = {}
    ) {
        //load all searchers
        async({
            return@async DB.get().catcher().getActiveCatchersWithRegexes()
        }, { catchers ->
            messages.forEach { sms ->
                testCatchers(sms, catchers, match)
            }

            then()
        })
    }


    /**
     * test sms for searchers
     */
    fun testCatchers(
        sms: SmsData, catchers: List<CatcherWithRegex>,
        match: (sms: SmsData) -> Unit = { }
    ) {
        //filter for senders
        var filtered =
            catchers.filter { it.catcher.sender == "" || it.catcher.sender == sms.sender }
        var matched = false
        filtered.filter { it.catcher.sender != "" }
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

        if (searchPattern.containsMatchIn(sms.body)) {
            match(sms)
            println("Search matched ${catcher.regex.regex} => ${sms.body}")
            val code = try {
                searchPattern.findAll(sms.body).toList().first().value
            } catch (e: Error) {
                ""
            }
            catcher.catcher.catchCount++
            copyToClipboard(code)
            //run actions if any
            runActions(sms, catcher)
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
                println("imported code $it $code")
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
    private fun runActions(message: SmsData, catcher: CatcherWithRegex) {

        if (catcher.actions.isNotEmpty()) {
            //if there is a action
            catcher.actions.forEach { action ->
                //generate action instance
                val instance = getInstance(action.action)
                instance?.let {
                    //if we found instance lets run it
                    instance.run(catcher, action, message)
                }
            }
        }

    }

    fun getInstance(className: String): BaseAction? {
        return when (className) {
            "NotificationAction" -> NotificationAction()
            "SmsAction" -> SmsAction()
            "TTSAction" -> TTSAction()
            "ClipboardAction" -> ClipboardAction()
            else -> null
        }
    }


    private fun copyToClipboard(code: String) {
        val clipboard =
            App.context()
                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("code-catcher", code)
        clipboard.setPrimaryClip(clip)


    }

}