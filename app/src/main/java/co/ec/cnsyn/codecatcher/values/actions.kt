package co.ec.cnsyn.codecatcher.values

import co.ec.cnsyn.codecatcher.database.action.Action
import java.util.Locale


private var actions = listOf(
    Action(1, "notification", "", "", "Notifications", "NotificationAction"),
    Action(2, "sms", "", "", "Textsms", "SmsAction"),
    Action(3, "copy", "", "", "ContentCopy", "ClipboardAction"),
    Action(4, "tts", "", "", "Mic", "TTSAction")
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
    "*" to mapOf(
        "notification" to "Notification",
        "sms" to "Sms",
        "copy" to "Copy",
        "tts" to "TTS",

        "notification-desc" to "Shows as a notification.",
        "sms-desc" to "Send codes with sms message",
        "copy-desc" to "Copy codes to clipboard.",
        "tts-desc" to "Read codes with TTS.",
    ),
)

fun actionList(): List<Action> {
    val locale = Locale.getDefault().toString()
    val actionLangDetails = actionLang[locale] ?: actionLang["*"]
    if (actionLangDetails == null) {
        return actions
    }
    return actions.map {
        it.name = actionLangDetails[it.key] ?: ""
        it.description = actionLangDetails[it.key + "-desc"] ?: ""
        return@map it
    }

}