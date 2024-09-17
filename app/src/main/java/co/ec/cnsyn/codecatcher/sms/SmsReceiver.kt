package co.ec.cnsyn.codecatcher.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsMessage
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import co.ec.cnsyn.codecatcher.BuildConfig
import co.ec.cnsyn.codecatcher.database.servicelog.ServiceLogDao
import co.ec.cnsyn.codecatcher.helpers.AppLogger
import co.ec.cnsyn.codecatcher.helpers.Settings
import co.ec.cnsyn.codecatcher.helpers.unix
import java.util.UUID

class SmsReceiver : BroadcastReceiver() {

    var receiverId: String = UUID.randomUUID().toString()

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {

            val activeReceiverId = Settings(context.applicationContext).getString("receiverId", "")
            if (activeReceiverId == receiverId) {
                //this is last registered
                val messages = getMessageFromIntent(intent)
                ActionRunner().runSmsList(messages)
            } else {
                if (activeReceiverId != "") {
                    AppLogger.d(
                        "Wrong receiverId unregister [$receiverId] active: [$activeReceiverId]",
                        "Receiver"
                    )
                }
            }

        }

    }

    private fun getMessageFromIntent(intent: Intent): List<SmsData> {
        val pduArray = intent.getSerializableExtra("pdus") as Array<*>
        val messages = mutableListOf<SmsData>()
        val now = unix()
        for (pdu in pduArray) {
            val message = SmsMessage.createFromPdu(pdu as ByteArray, "3gpp")
            val smsData = SmsData(message.originatingAddress ?: "", message.messageBody, now)
            messages.add(smsData)
        }
        return messages.toList()
    }


    companion object {


        /**
         * generate and register a receiver
         */
        fun register(context: Context): SmsReceiver {

            //generate new receiver and register it
            val receiverInstance = SmsReceiver()
            AppLogger.d("Register Sms Receiver [${receiverInstance.receiverId}]", "Receiver")
            registerReceiver(
                context, receiverInstance,
                IntentFilter("android.provider.Telephony.SMS_RECEIVED"),
                ContextCompat.RECEIVER_EXPORTED
            )
            Settings(context).putString("receiverId", receiverInstance.receiverId)
            ServiceLogDao.addNew(receiverInstance.receiverId)


            //if debug version register debug receiver too
            if (BuildConfig.DEBUG) {
                val debugReceiver = DebugSmsReceiver()
                registerReceiver(
                    context, debugReceiver,
                    IntentFilter("co.ec.cnsyn.codecatcher.DEBUG_SMS"),
                    ContextCompat.RECEIVER_EXPORTED
                )
            }
            return receiverInstance

        }
    }

}