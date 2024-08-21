package co.ec.cnsyn.codecatcher.sms.actions


import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.BitmapFactory
import android.os.Build
import android.speech.tts.TextToSpeech
import android.text.TextUtils.replace
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationCompat
import co.ec.cnsyn.codecatcher.App
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.composables.ParamOptionBox
import co.ec.cnsyn.codecatcher.composables.ParamValueBox
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithActions
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithRegex
import co.ec.cnsyn.codecatcher.helpers.translate
import co.ec.cnsyn.codecatcher.sms.SmsData
import co.ec.cnsyn.codecatcher.ui.theme.secondaryLight
import kotlinx.serialization.json.JsonNull.content
import kotlin.random.Random


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
            val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_foreground,
                BitmapFactory.Options().apply {
                    inScaled = true
                    inDensity = 240
                    inTargetDensity = 480
                })

            notificationBuilder = setupTexts(catcher, action, sms, notificationBuilder)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(largeIcon)
                .setColor(secondaryLight.toArgb())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
            notificationManager?.notify(
                action.actionId * Random.nextInt(1, 300),
                notificationBuilder.build()
            )
            return true
        } catch (e: Error) {
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
            val map = action.params()
            var title =
                if (map.keys.contains("notificationTitle")) map["notificationTitle"] else
                    App.context().getString(R.string.action_NotificationAction_notification_title)
            if (title == "") {
                title =
                    App.context().getString(R.string.action_NotificationAction_notification_title)
            }
            var content =
                if (map.keys.contains("notificationContent")) map["notificationContent"] else
                    App.context().getString(R.string.action_NotificationAction_notification_content)


            val matches = extractCode(catcher, sms)

            title = (title ?: "_sender_ Verification Code")
                .replace("_code_", matches)
                .replace("_sender_", sms.sender)
                .replace("_message_", sms.body)
            content = (content ?: "_code_\\n_message_")
                .replace("_code_", matches)
                .replace("_sender_", sms.sender)
                .replace("_message_", sms.body)
            builder.setContentTitle(title).setContentText(content)
        } catch (e: Exception) {
            builder.setContentTitle(sms.sender)
                .setContentText(sms.body)
        }
        return builder

    }


    @Composable
    override fun Settings(
        action: ActionDetail, then: (settings: Map<String, String>) -> Unit
    ) {
        var params by remember { mutableStateOf(action.action.params()) }

        Column {

            val defaultTitle =
                stringResource(id = R.string.action_NotificationAction_notification_title)
            ParamValueBox(
                stringResource(id = R.string.action_NotificationAction_notification_title_title),
                params["notificationTitle"] ?: defaultTitle,
                helperText = stringResource(id = R.string.action_NotificationAction_notification_hint),
            ) {
                val updatable = params.toMutableMap()
                updatable["notificationTitle"] = it
                params = updatable.toMap()
                action.action.updateParam(params)
                then(params)
            }

            val defaultContent =
                stringResource(id = R.string.action_NotificationAction_notification_content)
            ParamValueBox(
                stringResource(id = R.string.action_NotificationAction_notification_content_title),
                params["notificationContent"] ?: defaultContent,
                helperText = stringResource(id = R.string.action_NotificationAction_notification_hint),
            ) {
                val updatable = params.toMutableMap()
                updatable["notificationContent"] = it
                params = updatable.toMap()
                action.action.updateParam(params)
                then(params)
            }


        }
    }
}