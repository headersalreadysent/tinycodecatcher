package co.ec.cnsyn.codecatcher.database

import android.content.Context
import androidx.room.Room
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.catcheraction.CatcherAction
import co.ec.cnsyn.codecatcher.database.code.Code
import co.ec.cnsyn.codecatcher.helpers.unix
import co.ec.cnsyn.codecatcher.values.actionList
import co.ec.cnsyn.codecatcher.values.regexList
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

            val list = get().action().getAllItems()
            if (list.isEmpty()) {

                val regexes = regexList()
                get().regex().insertAll(*regexes.toTypedArray())
                get().action().insertAll(*actionList().toTypedArray())

                val catchers = List(3) { it ->
                    Catcher(
                        id = it + 1,
                        sender = "",
                        description = regexes[it].description,
                        regexId = it + 1
                    )
                }
                val catchersAction = mutableListOf<CatcherAction>()
                for (i in 1..3) {
                    val rands = randomAction(Random.nextInt(1, 4), 1..4)
                    rands.forEach { action ->
                        catchersAction.add(
                            CatcherAction(
                                catcherId = i,
                                actionId = action,
                                params = actionList()
                                    .find { it.id == action }?.defaultParams ?: "{}"

                            )
                        )
                    }
                }

                get().catcher().insertAll(*catchers.toTypedArray())
                get().catcherAction().insertAll(*catchersAction.toTypedArray())

                generateFakeData()
            }

        }

    }

    private fun generateFakeData() {
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
                    catcherId = Random.nextInt(1, 4),
                    sender = "Sender${Random.nextInt(1, 10)}",
                    sms = "Sample SMS text ${Random.nextInt(1, 100)}",
                    code = generateRandomCode(8)
                )
            )
            newDate--
        }
        get().code().insertAll(*codes.toTypedArray())
        //update counts
        get().catcher().fixCatchersCounts()
    }

    private fun randomAction(n: Int, range: IntRange): List<Int> {

        val randomNumbers = mutableSetOf<Int>()
        while (randomNumbers.size < n) {
            randomNumbers.add(Random.nextInt(range.first, range.last + 1))
        }
        return randomNumbers.toList()
    }
}