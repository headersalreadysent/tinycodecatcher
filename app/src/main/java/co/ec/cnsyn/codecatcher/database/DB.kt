package co.ec.cnsyn.codecatcher.database

import android.content.Context
import androidx.room.Room
import co.ec.cnsyn.codecatcher.App
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.code.Code
import co.ec.cnsyn.codecatcher.database.regex.Regex
import java.util.Date
import kotlin.concurrent.thread
import kotlin.random.Random

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
            fillDatabase()
            instance
        }
    }

    fun get(): AppDatabase {
        return INSTANCE!!;
    }


    private fun generateRandomCode(length: Int = 6): String {
        val chars = "0123456789"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }

    fun fillDatabase() {
        thread {
            get().clearAllTables()

            var list = get().regex().getAllItems()
            if (list.isEmpty()) {
                var regex = Regex(
                    id = 1,
                    regex = "[0-9]",
                    description = "",
                    catchCount = 1,
                    status = 1
                )
                get().regex().insert(regex);
                val catchers = List(6) { it ->
                    Catcher(
                        id = it + 1,
                        sender = "",
                        description = "",
                        regexId = regex.id
                    )
                }
                get().catcher().insertAll(*catchers.toTypedArray());
                val codes = List(100) {
                    Code(
                        date = Date().time, // Current timestamp
                        catcherId = Random.nextInt(1, 6),
                        sender = "Sender${Random.nextInt(1, 10)}",
                        sms = "Sample SMS text ${Random.nextInt(1, 100)}",
                        code = generateRandomCode(8)
                    )
                }
                get().code().insertAll(*codes.toTypedArray());
            }

        }

    }
}