package co.ec.cnsyn.codecatcher.helpers

import android.util.Log
import co.ec.cnsyn.codecatcher.App
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppLogger {
    private const val DEFAULT_TAG = "CodeCatcher"
    private const val LOG_FILE_NAME = "app_logs.txt"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private fun getTag(customTag: String?): String {
        return if (customTag.isNullOrEmpty()) {
            DEFAULT_TAG
        } else {
            "$DEFAULT_TAG - $customTag"
        }
    }

    private fun getCurrentTime(): String {
        return dateFormat.format(Date())
    }

    private fun appendLogToFile(message: String) {
        try {
            val logFile = File(App.context().filesDir, LOG_FILE_NAME)
            val writer = FileWriter(logFile, true)
            writer.append(message)
            writer.append("\n")
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            Log.e(DEFAULT_TAG, "Error writing log to file", e)
        }
    }

    fun d(message: String, customTag: String? = null) {
        val tag = getTag(customTag)
        Log.d(tag, message)

        if (!customTag.isNullOrEmpty()) {
            appendLogToFile("${getCurrentTime()}#$customTag#$message")
        }
    }

    fun e(message: String, throwable: Throwable? = null, customTag: String? = null) {
        val tag = getTag(customTag)
        Log.e(tag, message, throwable)

        if (!customTag.isNullOrEmpty()) {
            appendLogToFile("${getCurrentTime()}#$customTag#$message-${throwable?.message ?: throwable.toString()}")
        }
    }

    fun i(message: String, customTag: String? = null) {
        val tag = getTag(customTag)
        Log.i(tag, message)

        if (!customTag.isNullOrEmpty()) {
            appendLogToFile("${getCurrentTime()}#$customTag#$message")
        }
    }

    fun w(message: String, customTag: String? = null) {
        val tag = getTag(customTag)
        Log.w(tag, message)

        if (!customTag.isNullOrEmpty()) {
            appendLogToFile("${getCurrentTime()}#$customTag#$message")
        }
    }

    fun v(message: String, customTag: String? = null) {
        val tag = getTag(customTag)
        Log.v(tag, message)

        if (!customTag.isNullOrEmpty()) {
            appendLogToFile("${getCurrentTime()}#$customTag#$message")
        }
    }
}

