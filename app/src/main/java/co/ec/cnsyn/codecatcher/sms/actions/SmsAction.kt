package co.ec.cnsyn.codecatcher.sms.actions


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import co.ec.cnsyn.codecatcher.App
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithActions
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithRegex
import co.ec.cnsyn.codecatcher.helpers.async
import co.ec.cnsyn.codecatcher.sms.SmsData


class SmsAction : BaseAction {


    override fun run(catcher: CatcherWithRegex, action: CatcherWithActions, sms: SmsData): Boolean {
        sendSms(catcher, action, sms)
        return true
    }

    private fun sendSms(
        search: CatcherWithRegex,
        action: CatcherWithActions,
        sms: SmsData
    ): Boolean {
        val smsManager = SmsManager.getDefault()
        val params = try {
            action.params()
        } catch (e: Exception) {
            mapOf(
                "no" to "0",
                "sendType" to "sms"
            )
        }
        val context = App.context()
        if (context.checkSelfPermission(Manifest.permission.SEND_SMS)
            == PackageManager.PERMISSION_GRANTED
        ) {

            if (params.keys.contains("no") && params["no"] != "0") {
                // Send the SMS
                val sendType = if (params.keys.contains("sendType")) params["sendType"] else "sms"
                val smsBody = if (sendType == "sms") sms.body else extractCode(search, sms)
                smsManager.sendTextMessage(
                    params["no"], null, smsBody, null, null
                )
            } else {
                if (params["no"] == "0") {
                    Toast.makeText(App.context(), "no number", Toast.LENGTH_LONG).show()
                }
            }
            return true
        } else {
            Toast.makeText(context, "cant send sms", Toast.LENGTH_SHORT).show()
            return false
        }
    }
}