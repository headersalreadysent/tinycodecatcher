package co.ec.cnsyn.codecatcher.sms.actions


import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import co.ec.cnsyn.codecatcher.App
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithActions
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithRegex
import co.ec.cnsyn.codecatcher.helpers.translate
import co.ec.cnsyn.codecatcher.sms.SmsData


class NotificationAction : BaseAction {

    override fun run(catcher: CatcherWithRegex, action: CatcherWithActions, sms: SmsData): Boolean {
        val context = App.context()
        val notificationManager: NotificationManager? =
            context.getSystemService(NotificationManager::class.java)

        if (notificationManager?.areNotificationsEnabled() == false) {
            Toast.makeText(
                context,
                translate("action_NotificationAction_permission_error"), Toast.LENGTH_LONG
            ).show()
            return false
        }

        //generate channel
        try {
            val channelId = "code-catcher-${catcher.catcher.id}"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, action.action, importance)
                notificationManager?.createNotificationChannel(channel)
            }
            //extract details
            var notificationBuilder = NotificationCompat.Builder(context, channelId)

            notificationBuilder = setupTexts(catcher, action, sms, notificationBuilder)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            notificationManager?.notify(456, notificationBuilder.build())
            return true
        } catch(e:Error) {
            Toast.makeText(
                context,
                translate("action_NotificationAction_error"), Toast.LENGTH_LONG
            ).show()
            return false
        }

    }

    /**
     * extract details from sms
     */
    private fun setupTexts(
        catcher: CatcherWithRegex,
        action: CatcherWithActions,
        sms: SmsData,
        builder: NotificationCompat.Builder
    ): NotificationCompat.Builder {
        try {
            /*
            {"title":"Code Received from _title_","content":"Received code: _code_"}
             */
            val map = action.params()
            val matches = catcher.regex.regex.toRegex().findAll(sms.body).toList()
            builder.setContentTitle(sms.sender).setContentText(sms.body)
        } catch (e: Exception) {
            builder.setContentTitle(sms.sender)
                .setContentText(sms.body)
        }
        return builder

    }
}