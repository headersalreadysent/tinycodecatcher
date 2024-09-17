package co.ec.cnsyn.codecatcher.pages.help

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import co.ec.cnsyn.codecatcher.App
import co.ec.cnsyn.codecatcher.sms.SmsService

class HelpViewModel : ViewModel() {

    /**
     * open channel settings
     */
    fun openChannelSettings() {
        val context = App.context()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS,).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, SmsService.CHANNEL_NAME)
            }
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

    }
}