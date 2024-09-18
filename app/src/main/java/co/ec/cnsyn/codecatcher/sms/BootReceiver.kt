package co.ec.cnsyn.codecatcher.sms


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import co.ec.cnsyn.codecatcher.helpers.AppLogger

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            AppLogger.d( "Boot Receiver Start With Boot Complete","BootReceiver")
        } else {
            AppLogger.d( "Boot Receiver Start With Alarm Receiver","BootReceiver")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(Intent(context, SmsService::class.java))
        } else {
            context?.startService(Intent(context, SmsService::class.java))
        }
    }


}