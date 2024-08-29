package co.ec.cnsyn.codecatcher

import co.ec.cnsyn.codecatcher.database.AppDatabase


import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import co.ec.cnsyn.codecatcher.database.DB
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class App : Application() {

    companion object {

        private lateinit var instance: App

        fun context(): Context {
            return instance.applicationContext
        }

        fun contextCheck(): Context? {
            return if (::instance.isInitialized) instance.applicationContext else null
        }
    }

    val database: AppDatabase by lazy {
        DB.getDatabase(this)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        instance = this

        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        Log.d("AndroidID", "Your Android ID: $androidId")

        val packageManager = packageManager
        val componentName = ComponentName(this, DebugActivity::class.java)

        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(applicationContext))

        if (androidId == "34d9b86b53ed2963") {
            packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }

    }

}