package co.ec.cnsyn.codecatcher.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsMessage
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import co.ec.cnsyn.codecatcher.helpers.unix

class SmsReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
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
        fun register(context: Context) {

            Log.d("CodeCatcherService", "Receiver Register")
            registerReceiver(
                context,
                SmsReceiver(),
                IntentFilter("android.provider.Telephony.SMS_RECEIVED"),
                ContextCompat.RECEIVER_EXPORTED
            )

        }
    }

}