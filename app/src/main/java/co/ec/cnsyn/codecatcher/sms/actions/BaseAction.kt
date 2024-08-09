package co.ec.cnsyn.codecatcher.sms.actions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithActions
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithRegex
import co.ec.cnsyn.codecatcher.sms.SmsData

interface BaseAction {

    fun run(catcher: CatcherWithRegex, action: CatcherWithActions, sms: SmsData): Boolean


    fun extractCode(catcher: CatcherWithRegex, sms: SmsData): String {
        return try {
            //get values from map
            val matches = catcher.regex.regex.toRegex().findAll(sms.body).toList()
            return matches[0].value
        } catch (e: Exception) {
            sms.body
        }
    }

    fun baseParams(): Map<String, String> {
        return mapOf()
    }


    @Composable
    fun Settings(action: ActionDetail, then: (settings: Map<String, String>) -> Unit) {
        Text(
            text = "No settings", modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.Center
            )
        )
    }

}