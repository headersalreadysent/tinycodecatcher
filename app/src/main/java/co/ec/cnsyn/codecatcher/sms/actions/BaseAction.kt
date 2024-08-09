package co.ec.cnsyn.codecatcher.sms.actions

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

}