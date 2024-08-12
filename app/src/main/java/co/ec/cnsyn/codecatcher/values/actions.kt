package co.ec.cnsyn.codecatcher.values

import co.ec.cnsyn.codecatcher.database.action.Action
import co.ec.cnsyn.codecatcher.helpers.translate


private var actions = listOf(
    Action(
        1,
        key = "notification",
        icon = "Notifications",
        action = "NotificationAction"
    ),
    Action(
        2,
        key = "sms",
        icon = "Textsms",
        action = "SmsAction",
        defaultParams = "{\"no\":\"\",\"sendType\":\"code\"}"
    ),
    Action(
        3,
        key = "copy",
        icon = "ContentCopy",
        action = "ClipboardAction"
    ),
    Action(
        4,
        key = "tts",
        icon = "Mic",
        action = "TTSAction"
    )
)

var actionLang = mapOf(
    "tr_TR" to mapOf(
        "notification" to "Bildirim",
        "sms" to "Sms",
        "copy" to "Kopyala",
        "tts" to "Sesli Oku",

        "notification-desc" to "Kodları bildirim olarak gösterir.",
        "sms-desc" to "Kodları sms ile gönderir.",
        "copy-desc" to "Kodları panoya kopyalar.",
        "tts-desc" to "Kodları sesli olarak okur.",
    ),
)

fun actionList(): List<Action> {
    return actions.map {
        it.name = translate("actionlist_${it.key}")
        it.description = translate("actionlist_${it.key}_desc")
        return@map it
    }

}