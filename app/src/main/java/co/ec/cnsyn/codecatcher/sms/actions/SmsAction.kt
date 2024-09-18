package co.ec.cnsyn.codecatcher.sms.actions


import android.Manifest
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import co.ec.cnsyn.codecatcher.App
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.composables.ParamOptionBox
import co.ec.cnsyn.codecatcher.composables.ParamValueBox
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithActions
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithRegex
import co.ec.cnsyn.codecatcher.helpers.translate
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
        val smsManager =  SmsManager.getDefault()
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
            try {
                if (params.keys.contains("no") && params["no"] != "0" && params["no"] != "") {
                    // Send the SMS
                    val sendType =
                        if (params.keys.contains("sendType")) params["sendType"] else "sms"
                    val smsBody = if (sendType == "sms") sms.body else extractCode(search, sms)
                    smsManager.sendTextMessage(
                        params["no"], null, smsBody, null, null
                    )
                } else {
                    Toast.makeText(
                        App.context(),
                        translate("action_SmsAction_forward_no_error"),
                        Toast.LENGTH_LONG
                    ).show()
                }
                return true
            } catch (err: Error) {

                Toast.makeText(
                    App.context(),
                    translate("action_SmsAction_error"),
                    Toast.LENGTH_LONG
                ).show()
                return false
            }
        } else {
            Toast.makeText(
                context,
                translate("action_SmsAction_permission_error"),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
    }

    @Composable
    override fun Settings(
        action: ActionDetail, then: (settings: Map<String, String>) -> Unit
    ) {
        var params by remember { mutableStateOf(action.action.params()) }

        Column {
            //update phone number
            ParamValueBox(
                stringResource(id = R.string.action_SmsAction_forward_number),
                params["no"] ?: "",
                keyboardType = KeyboardOptions(keyboardType = KeyboardType.Number),
            ) {
                val updatable = params.toMutableMap()
                updatable["no"] = it
                params=updatable.toMap()
                action.action.updateParam(params)
                then(params)
            }
            ParamOptionBox(
                stringResource(id = R.string.action_SmsAction_forward_send_type),
                params["sendType"] ?: "sms",
                listOf(
                    Pair(
                        "sms",
                        stringResource(id = R.string.action_SmsAction_forward_send_type_sms)
                    ),
                    Pair(
                        "code",
                        stringResource(id = R.string.action_SmsAction_forward_send_type_code)
                    )
                )
            ) {
                val updatable = params.toMutableMap()
                updatable["sendType"] = it
                params=updatable.toMap()
                action.action.updateParam(params)
                then(params)
            }
        }
    }
}