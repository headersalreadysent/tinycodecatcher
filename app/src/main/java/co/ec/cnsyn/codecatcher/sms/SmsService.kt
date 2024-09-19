package co.ec.cnsyn.codecatcher.sms

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import androidx.core.app.NotificationCompat
import co.ec.cnsyn.codecatcher.MainActivity
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.database.servicelog.ServiceLogDao
import co.ec.cnsyn.codecatcher.helpers.AppLogger
import co.ec.cnsyn.codecatcher.helpers.formatTime
import co.ec.cnsyn.codecatcher.helpers.unix
import java.util.UUID
import co.ec.cnsyn.codecatcher.helpers.Settings as AppSettings


class SmsService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private var runCount = 0
    private var serviceId: String = UUID.randomUUID().toString()

    var hashes = ""


    companion object {
        const val CHANNEL_NAME = "CodeCatcher-Service-Channel"
        const val HEART_BEAT_DELAY = 10

        const val SERVICE_LIFE_IN_SECONDS = 86400 * 365

        var receiver: SmsReceiver? = null

        /**
         * check service running in android
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
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        SystemClock.elapsedRealtime() + 1000 * timeout,
                        pendingIntent
                    )
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //add channel with low importance
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(
                    CHANNEL_NAME, CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN
                )
            )
        }

        val settings = AppSettings(applicationContext)
        runCount = 0
        runnable = object : Runnable {
            override fun run() {
                try {
                    if (receiver == null) {
                        //if no receiver exists stop and restart yourself
                        throw Error("Stop service because no receiver exists")
                    }
                    runCount++
                    //ad heartbeat
                    settings.putInt("service-heartbeat", unix().toInt())
                    settings.putInt("service-pulse", runCount)
                    ServiceLogDao.beat(receiver, HEART_BEAT_DELAY)
                    if (runCount % 10 == 0) {
                        //log to system if 10 times of run
                        AppLogger.d(
                            "Service Running Time: ${(runCount * HEART_BEAT_DELAY).formatTime()}  [$hashes]",
                            "service"
                        )
                    }
                    if (runCount < SERVICE_LIFE_IN_SECONDS / HEART_BEAT_DELAY) {
                        //if receiver is not null and 1 day still running
                        handler.postDelayed(this, HEART_BEAT_DELAY * 1000L)
                    } else {
                        //kill receiver
                        AppLogger.d(
                            "Service Life ${SERVICE_LIFE_IN_SECONDS.formatTime()} is finish self stop",
                            "service"
                        )
                        stopSelf()

                    }
                } catch (e: Throwable) {
                    AppLogger.e(e.message.toString(), e, "service")
                    stopSelf()
                }


            }
        }
        // Start the timer
        handler.post(runnable)


    }

    /**
     * start service
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start the service in the foreground
        // double start is hack for hiding notification
        try {
            startForeground(45, generateServiceNotification())
            //re register sms receiver
            AppLogger.d("Sms Service generated [$serviceId]", "service")
            receiver = SmsReceiver.register(applicationContext)
            //calculate hashes
            hashes = serviceId.split("-").last() + " - " + (receiver?.receiverId?.split("-")
                ?.last() ?: "")
            AppLogger.d("Receiver added", "service")
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
        try {
            receiver?.let {
                applicationContext.unregisterReceiver(it)
                AppLogger.d("Unregister Sms Receiver [${receiver?.receiverId}]", "Receiver")
                receiver = null
            }
        } catch (_: Throwable) {
        }
        setupService(applicationContext)
    }


    private fun generateServiceNotification(): Notification {

        //setup notification
        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        notificationIntent.putExtra("destination", "help")
        notificationIntent.putExtra("destinationParam", "service_notification")

        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_NAME)
            .setContentTitle(applicationContext.getString(R.string.smsservice_notification_title))
            .setContentText(applicationContext.getString(R.string.smsservice_notification_content))
            .setSound(null)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setContentIntent(pendingIntent)
            .build()
    }


}