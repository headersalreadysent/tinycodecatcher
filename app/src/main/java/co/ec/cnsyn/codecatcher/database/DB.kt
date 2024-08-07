package co.ec.cnsyn.codecatcher.database

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Textsms
import androidx.room.Room
import co.ec.cnsyn.codecatcher.App
import co.ec.cnsyn.codecatcher.database.action.Action
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.catcheraction.CatcherAction
import co.ec.cnsyn.codecatcher.database.code.Code
import co.ec.cnsyn.codecatcher.database.regex.Regex
import co.ec.cnsyn.codecatcher.helpers.unix
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

            var list = get().action().getAllItems()
            if (list.isEmpty()) {

                //generate actions in db
                get().action().insertAll(
                    *arrayOf(
                        Action(1, "SMS", "Textsms", "SmsAction"),
                        Action(2, "Copy", "ContentCopy", "CopyAction"),
                        Action(3, "TTS", "Mic", "TTSAction")
                    )
                )

                get().regex().insert(
                    Regex(
                        id = 1,
                        regex = "[0-9]",
                        description = "",
                        catchCount = 1,
                        status = 1
                    )
                );
                val catchers = List(6) { it ->
                    Catcher(
                        id = it + 1,
                        sender = "",
                        description = "",
                        regexId = 1
                    )
                }
                val catchersAction = mutableListOf<CatcherAction>()
                var k = 1;
                for (i in 1..6) {
                    var size = Random.nextInt(1, 3)
                    for (j in 1..size) {
                        catchersAction.add(
                            CatcherAction(
                                id = k,
                                catcherId = i,
                                actionId = j
                            )
                        )
                        k++
                    }
                }

                get().catcher().insertAll(*catchers.toTypedArray());
                get().catcherAction().insertAll(*catchersAction.toTypedArray());
                var codes = mutableListOf<Code>()
                var newDate = 0
                var date = unix()
                for (i in 1..150) {
                    if (newDate == 0) {
                        newDate = Random.nextInt(2, 6)
                        date -= 86400
                    }
                    codes.add(
                        Code(
                            date = date,
                            catcherId = Random.nextInt(1, 6),
                            sender = "Sender${Random.nextInt(1, 10)}",
                            sms = "Sample SMS text ${Random.nextInt(1, 100)}",
                            code = generateRandomCode(8)
                        )
                    )
                    newDate--
                }
                get().code().insertAll(*codes.toTypedArray());
            }

        }

    }
}