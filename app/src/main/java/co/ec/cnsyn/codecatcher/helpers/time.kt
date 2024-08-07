package co.ec.cnsyn.codecatcher.helpers

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun unix(): Long = (System.currentTimeMillis() / 1000F).toLong()

fun Long.dateString(format: String = "dd.MM.yyyy"): String {
    val date = Date(this * 1000) // Convert seconds to milliseconds
    val formatter = SimpleDateFormat(format, Locale.getDefault())
    return formatter.format(date)
}

fun Long.timeString(): String {
    val date = Date(this * 1000) // Convert seconds to milliseconds
    val formatter = SimpleDateFormat("HH.mm", Locale.getDefault())
    return formatter.format(date)
}

