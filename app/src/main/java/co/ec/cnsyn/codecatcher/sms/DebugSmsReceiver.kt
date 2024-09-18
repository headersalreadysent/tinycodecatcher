package co.ec.cnsyn.codecatcher.sms


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.ec.cnsyn.codecatcher.helpers.unix

class DebugSmsReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "co.ec.cnsyn.codecatcher.DEBUG_SMS") {
            val sender = intent.getStringExtra("sender")
            val message = intent.getStringExtra("message") ?: "hello 1234"

            ActionRunner().runSmsList(
                listOf(
                    SmsData(sender ?: "", message.replace("_", " "), unix())
                )
            )
        }
    }


}