package co.ec.cnsyn.codecatcher.database

import android.content.Context
import androidx.room.Room

object DB {
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }

    fun get(): AppDatabase {
        return INSTANCE!!;
    }
}