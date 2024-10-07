package co.ec.cnsyn.codecatcher.helpers

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun unix(): Long = (System.currentTimeMillis() / 1000F).toLong()

fun Long.dateString(format: String? = null): String {
    val date = Date(this * 1000) // Convert seconds to milliseconds

    return if (format != null) {
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        formatter.format(date)
    } else {
        val dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault())
        dateFormat.format(date)
    }
}

fun Long.timeString(): String {
    val date = Date(this * 1000) // Convert seconds to milliseconds
    val formatter = SimpleDateFormat("HH.mm", Locale.getDefault())
    return formatter.format(date)
}


fun Int.formatTime(): String {
    val days = this / 86400
    val hours = (this - days * 86400) / 3600
    val minutes = (this % 3600) / 60
    val secs = this % 60
    val builder = StringBuilder()
    if (days > 0) {
        builder.append(String.format(Locale.getDefault(), "%02d:", days))
    }
    if (hours > 0) {
        builder.append(String.format(Locale.getDefault(), "%02d:", hours))
    }
    builder.append(String.format(Locale.getDefault(), "%02d:%02d", minutes, secs))
    return builder.toString()
}

