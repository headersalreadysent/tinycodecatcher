package co.ec.cnsyn.codecatcher

import android.content.Context
import android.content.Intent
import android.util.Log
import co.ec.cnsyn.codecatcher.helpers.AppLogger
import co.ec.cnsyn.codecatcher.helpers.Settings
import co.ec.cnsyn.codecatcher.helpers.dateString
import co.ec.cnsyn.codecatcher.helpers.timeString
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter


class ExceptionHandler(private val context: Context) : Thread.UncaughtExceptionHandler {
    private val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    @OptIn(DelicateCoroutinesApi::class)
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // Log the exception
        AppLogger.e("Uncaught exception in thread ${thread.name}", throwable, "exception")

        // Record the exception to a file
        recordExceptionToFile(throwable)

        val restartCount = Settings(context).getInt("appRestartAfterError", 0)
        if (restartCount < 3) {
            //try only 3 time
            AppLogger.w("try to restart for $restartCount", "exception")
            GlobalScope.launch {
                delay(2000L)
                Settings(context).putInt("appRestartAfterError", restartCount + 1)
                //restart application
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                context.startActivity(intent)
            }
        } else {
            // Pass the exception to the default handler (optional)
            defaultExceptionHandler?.uncaughtException(thread, throwable)
        }

    }


    private fun recordExceptionToFile(throwable: Throwable) {
        val logDir = getLogDirectory(context)
        val fileName = "crash_report_${System.currentTimeMillis() / 1000L}.txt"

        val file = File(logDir, fileName)
        FileOutputStream(file).use { fos ->
            PrintWriter(fos).use { writer ->
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                throwable.printStackTrace(pw)
                writer.println(sw.toString())
            }
        }
    }

    companion object {

        fun getLogDirectory(context: Context): File {
            val logDir = File(context.filesDir, "crash")
            if (!logDir.exists()) {
                logDir.mkdirs() // Create the directory if it doesn't exist
            }
            return logDir
        }

        fun readExceptionLogs(context: Context): List<Pair<String, String>> {
            val logs = mutableMapOf<String, String>()
            val logDir = getLogDirectory(context)
            val logFiles = logDir.listFiles { _, name -> name.startsWith("crash_report_") }
                ?.sortedByDescending { it.name } ?: listOf()

            logFiles.forEach { file ->
                file.bufferedReader().use { reader ->
                    val date = file.name.replace("crash_report_", "").replace(".txt", "").toLong()
                    val name = date.dateString() + " " + date.timeString()
                    logs[name] = reader.readText()
                }
            }
            return logs.toList()
        }

        fun clearCrashLogs(context: Context) {
            val logDir = getLogDirectory(context)
            val logFiles = logDir.listFiles { _, name -> name.startsWith("crash_report_") }

            logFiles?.forEach { file ->
                if (file.exists()) {
                    file.delete()
                }
            }
        }

        fun readAppLogs(context: Context): List<String> {
            val logFile = File(context.filesDir, "app_logs.txt")
            val maxFileSize = 1 * 1024 * 1024 // 1 MB

            if (!logFile.exists()) return emptyList()

            // Check if the file exceeds 1 MB and trim if necessary
            if (logFile.length() > maxFileSize) {
                trimLogFile(logFile)
            }

            // Read the file in reverse
            val reversedLogs = mutableListOf<String>()
            logFile.readLines().asReversed().forEach { line ->
                reversedLogs.add(line)
            }

            return reversedLogs
        }

        private fun trimLogFile(logFile: File) {
            try {
                val lines = logFile.readLines()
                val halfSize = lines.size / 2
                val remainingLines = lines.subList(halfSize, lines.size)

                // Rewrite the file with the remaining lines
                logFile.writeText(remainingLines.joinToString("\n"))
            } catch (e: IOException) {
                Log.e("AppLogger", "Error trimming log file", e)
            }
        }

        fun clearAppLogs(context: Context) {

            val logFile = File(context.filesDir, "app_logs.txt")
            logFile.writeText("")
        }

    }
}