package co.ec.cnsyn.codecatcher

import co.ec.cnsyn.codecatcher.database.AppDatabase


import android.app.Application
import co.ec.cnsyn.codecatcher.database.DB

class App : Application() {
    val database: AppDatabase by lazy {
        DB.getDatabase(this)
    }
}