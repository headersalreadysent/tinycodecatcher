package co.ec.cnsyn.codecatcher.pages.help

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat.startActivity
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
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, SmsService.CHANNEL_NAME)
            }
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

    }

    fun openPermissionSettings(){
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", App.context().packageName, null)
            flags = FLAG_ACTIVITY_NEW_TASK
        }
        App.context().startActivity(intent)
    }
}