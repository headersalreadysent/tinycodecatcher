package co.ec.cnsyn.codecatcher.sms

import android.app.Service.RECEIVER_EXPORTED
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsMessage
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import co.ec.cnsyn.codecatcher.BuildConfig
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.servicelog.ServiceLog
import co.ec.cnsyn.codecatcher.database.servicelog.ServiceLogDao
import co.ec.cnsyn.codecatcher.helpers.Settings
import co.ec.cnsyn.codecatcher.helpers.async
import co.ec.cnsyn.codecatcher.helpers.dateString
import co.ec.cnsyn.codecatcher.helpers.unix
import java.util.UUID

class SmsReceiver : BroadcastReceiver() {

    var receiverId: String = UUID.randomUUID().toString()

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {

            val activeReceiverId = Settings(context.applicationContext).getString("receiverId", "")
            if (activeReceiverId != receiverId) {
                //this. is not latest receiver unregister this
                context.applicationContext.unregisterReceiver(this)
                return
            }

            val messages = getMessageFromIntent(intent)
            ActionRunner().runSmsList(messages)
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

        private var receiverInstance: SmsReceiver? = null
        private var debugReceiver: DebugSmsReceiver? = null

        fun register(context: Context): SmsReceiver {
            Log.d("CodeCatcherService", "Receiver Register")
            receiverInstance?.let {
                context.unregisterReceiver(receiverInstance)
            }
            receiverInstance = SmsReceiver()
            registerReceiver(
                context,
                receiverInstance,
                IntentFilter("android.provider.Telephony.SMS_RECEIVED"),
                ContextCompat.RECEIVER_EXPORTED
            )
            receiverInstance?.let {
                Settings(context).putString("receiverId", it.receiverId)
                ServiceLogDao.addNew(it.receiverId)
            }

            if (BuildConfig.DEBUG) {
                debugReceiver?.let {
                    context.unregisterReceiver(debugReceiver)
                }
                debugReceiver = DebugSmsReceiver()
                registerReceiver(
                    context,
                    debugReceiver,
                    IntentFilter("co.ec.cnsyn.codecatcher.DEBUG_SMS"),
                    ContextCompat.RECEIVER_EXPORTED
                )
            }
            return receiverInstance as SmsReceiver;

        }
    }

}