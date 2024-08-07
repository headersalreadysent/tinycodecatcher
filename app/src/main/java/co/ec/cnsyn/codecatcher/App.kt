package co.ec.cnsyn.codecatcher

import co.ec.cnsyn.codecatcher.database.AppDatabase


import android.app.Application
import android.content.Context
import co.ec.cnsyn.codecatcher.database.DB

class App : Application() {

    companion object {

        private lateinit var instance: App

        fun context(): Context {
            return instance.applicationContext
        }
    }

    val database: AppDatabase by lazy {
        DB.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}