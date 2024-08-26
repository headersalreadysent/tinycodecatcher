package co.ec.cnsyn.codecatcher.sms


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            Log.d("CodeCatcherService", "Boot Completed Receiver")
            context?.startService(Intent(context, SmsService::class.java))
        } else {
            Log.d("CodeCatcherService", "Alarm Receiver")
            context?.startService(Intent(context, SmsService::class.java))
        }
    }


}