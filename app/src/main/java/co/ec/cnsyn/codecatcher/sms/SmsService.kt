package co.ec.cnsyn.codecatcher.sms

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import co.ec.cnsyn.codecatcher.BuildConfig
import co.ec.cnsyn.codecatcher.MainActivity
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.servicelog.ServiceLog
import co.ec.cnsyn.codecatcher.database.servicelog.ServiceLogDao
import co.ec.cnsyn.codecatcher.helpers.AppLogger
import co.ec.cnsyn.codecatcher.helpers.dateString
import co.ec.cnsyn.codecatcher.helpers.formatTime
import co.ec.cnsyn.codecatcher.helpers.unix
import kotlinx.serialization.json.JsonNull.content
import java.util.UUID
import co.ec.cnsyn.codecatcher.helpers.Settings as AppSettings


class SmsService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private var runCount = 0
    private var serviceId: String = UUID.randomUUID().toString()


    companion object {
        const val channelName = "CodeCatcher-Service-Channel"
        const val heartBeatDelay = 10

        const val serviceLifeInSecond = 1800

        var receiver: SmsReceiver? = null

        /**
         * check service runnin in android
         */
        private fun isServiceRunning(context: Context, serviceClass: Class<out Any>): Boolean {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }

        //setup service with alarm manager
        fun setupService(context: Context, timeout: Int = 0) {
            if (isServiceRunning(context, SmsService::class.java)) {
                //if already running return
                return
            }
            AppLogger.d("Setup Service with alarm manager", "service")
            val pendingIntent =
                PendingIntent.getBroadcast(
                    context, 0,
                    Intent(context, BootReceiver::class.java),
                    PendingIntent.FLAG_MUTABLE
                )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    // If your app does not have the permission, guide the user to the settings
                    AppLogger.d("Setup service request alarm", "service")
                    val scheduleIntent = Intent().apply {
                        action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                        data = android.net.Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(scheduleIntent)
                } else {
                    AppLogger.d("Setup service schedule", "service")
                    alarmManager.setExact(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 1000 * timeout,
                        pendingIntent
                    )
                }
            } else {

                AppLogger.d("Setup service old version", "service")
                alarmManager.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 1000 * timeout,
                    pendingIntent
                )
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val settings = AppSettings(applicationContext)

        runCount = 0
        runnable = object : Runnable {
            override fun run() {
                if (receiver == null) {
                    //if no receiver exists stop and restart yourself
                    AppLogger.d("Stop service because no receiver exists", "service")
                    stopSelf()
                    return
                }
                runCount++;
                if (runCount % 10 == 0) {
                    //log to system if 10 times of run
                    val hashes = serviceId.split("-").last() + " - " + (receiver?.receiverId?.split("-")
                        ?.last() ?: "")
                    AppLogger.d(
                        "Service Running Time: ${(runCount * heartBeatDelay).formatTime()}  [$hashes]",
                        "service"
                    )
                }
                //ad heartbeat
                settings.putInt("service-heartbeat", unix().toInt())
                settings.putInt("service-pulse", runCount)
                receiver?.let {
                    ServiceLogDao.beat(it.receiverId, heartBeatDelay)
                }
                if (runCount < serviceLifeInSecond / heartBeatDelay) {
                    //if receiver is not null and 1 day still running
                    handler.postDelayed(this, heartBeatDelay * 1000L)
                } else {
                    //every day restart yourself
                    AppLogger.d(
                        "Service Life ${serviceLifeInSecond.formatTime()} is finish self stop",
                        "service"
                    )
                    stopSelf()
                }
            }
        }
        // Start the timer
        handler.post(runnable)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //add channel with low importance
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(
                    channelName, channelName, NotificationManager.IMPORTANCE_MIN
                )
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //setup notification
        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        notificationIntent.putExtra("destination", "help")
        notificationIntent.putExtra("destinationParam", "service_notification")

        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, channelName)
            .setContentTitle(applicationContext.getString(R.string.smsservice_notification_title))
            .setContentText(applicationContext.getString(R.string.smsservice_notification_content))
            .setSound(null)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setContentIntent(pendingIntent)
            .build()

        // Start the service in the foreground
        // double start is hack for hiding notification
        try {
            startForeground(45, notification)
            //re register sms receiver
            AppLogger.d("Sms Service generated [$serviceId]", "service")
            receiver = SmsReceiver.register(applicationContext)
        } catch (e: Error) {
            //re setup
            AppLogger.e("Service starting error", e, "service")
            stopSelf()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onDestroy() {
        super.onDestroy()
        //on destroy re setup
        AppLogger.d("Service destroyed", "service")
        receiver?.let {
            applicationContext.unregisterReceiver(it)
            receiver = null
        }
        setupService(applicationContext)
    }


}