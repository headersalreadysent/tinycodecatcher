package co.ec.cnsyn.codecatcher.sms.actions


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import co.ec.cnsyn.codecatcher.App
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithActions
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithRegex
import co.ec.cnsyn.codecatcher.sms.SmsData


class ClipboardAction : BaseAction {


    override fun run(catcher: CatcherWithRegex, action: CatcherWithActions, sms: SmsData): Boolean {
        val context = App.context()

        //generate content
        val code = extractCode(catcher, sms)
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("code-catcher", code)
        clipboard.setPrimaryClip(clip)

        return true
    }
}