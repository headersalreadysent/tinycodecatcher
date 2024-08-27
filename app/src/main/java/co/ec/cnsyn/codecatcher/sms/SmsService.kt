package co.ec.cnsyn.codecatcher.sms

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
import android.util.Log
import androidx.core.app.NotificationCompat
import co.ec.cnsyn.codecatcher.MainActivity
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.helpers.unix
import kotlinx.serialization.json.JsonNull.content
import co.ec.cnsyn.codecatcher.helpers.Settings as AppSettings


class SmsService : Service() {


    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private var runCount = 0


    companion object {
        const val channelName = "CodeCatcher-Service-Channel"
        const val heartBeatDelay = 10

        var receiver: SmsReceiver? = null

        //setup service with alarm manager
        fun setupService(context: Context, timeout: Int = 0) {
            Log.d("CodeCatcherService", "Set Alarm Manager")
            val intent = Intent(context, BootReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_MUTABLE
            )
            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    // If your app does not have the permission, guide the user to the settings
                    val scheduleIntent = Intent().apply {
                        action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                        data = android.net.Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(scheduleIntent)
                } else {
                    alarmManager.setExact(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 1000 * timeout, pendingIntent
                    )
                }
            } else {
                alarmManager.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 1000 * timeout, pendingIntent
                )
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        var settings = AppSettings(applicationContext)

        runCount = 0
        runnable = object : Runnable {
            override fun run() {
                // Print the current run
                runCount++;
                Log.d("CodeCatcherService", "Service Running: $runCount times")
                settings.putInt("service-heartbeat", unix().toInt())
                settings.putInt("service-pulse", runCount)
                if (runCount < 86400 / heartBeatDelay) {
                    handler.postDelayed(this, heartBeatDelay * 1000L)
                } else {
                    //every day restart yourself
                    Log.d("CodeCatcherService", "self stop")
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

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {



        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        notificationIntent.putExtra("destination", "help")
        notificationIntent.putExtra("destinationParam","service_notification")

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
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
        startForeground(45, notification)


        //re register sms receiver
        receiver = SmsReceiver.register(applicationContext)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onDestroy() {
        super.onDestroy()
        //on destroy re setup
        Log.d("CodeCatcherService", "destroy")
        receiver?.let {
            applicationContext.unregisterReceiver(it)
            receiver = null
        }
        setupService(applicationContext)
    }

}