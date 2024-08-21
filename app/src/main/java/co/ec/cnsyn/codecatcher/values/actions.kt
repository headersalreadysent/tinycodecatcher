package co.ec.cnsyn.codecatcher.values

import co.ec.cnsyn.codecatcher.database.action.Action
import co.ec.cnsyn.codecatcher.helpers.translate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


private var actions = listOf(
    Action(
        1,
        key = "notification",
        icon = "Notifications",
        action = "NotificationAction",
        defaultParams = Json.encodeToString(
            mapOf(
                "notificationTitle" to translate("action_NotificationAction_notification_title"),
                "notificationContent" to translate("action_NotificationAction_notification_content")
            )
        )

    ),
    Action(
        2,
        key = "sms",
        icon = "Textsms",
        action = "SmsAction",
        defaultParams = Json.encodeToString(
            mapOf(
                "no" to "",
                "sendType" to "code"
            )
        )
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
        action = "TTSAction",
        defaultParams = Json.encodeToString(
            mapOf(
                "adjustVolume" to "yes",
                "sendType" to "code"
            )
        )
    )
)

fun actionList(): List<Action> {
    return actions.map {
        it.name = translate("actionlist_${it.key}")
        it.description = translate("actionlist_${it.key}_desc")
        return@map it
    }

}