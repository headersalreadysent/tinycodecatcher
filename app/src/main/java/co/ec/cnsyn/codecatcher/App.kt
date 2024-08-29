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
import co.ec.cnsyn.codecatcher.sms.SmsService
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

    override fun onCreate() {
        super.onCreate()
        instance = this
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(applicationContext))


        SmsService.setupService(applicationContext)
    }

}