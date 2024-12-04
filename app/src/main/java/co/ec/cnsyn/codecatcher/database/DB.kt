package co.ec.cnsyn.codecatcher.database

import android.content.Context
import androidx.room.Room
import co.ec.cnsyn.codecatcher.BuildConfig
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
        return INSTANCE!!
    }


    private fun fillDatabase() {
        thread {

            val list = get().action().getAllItems()
            if (list.isEmpty()) {

                val regexes = regexList()
                get().regex().insertAll(*regexes.toTypedArray())
                get().action().insertAll(*actionList().toTypedArray())

                if (BuildConfig.DEBUG) {
                    //if debug generate some records to testing
                    val catchers = List(regexes.size) {
                        Catcher(
                            id = it + 1,
                            sender = "",
                            description = regexes[it].description,
                            regexId = it + 1
                        )
                    }
                    val catchersAction = mutableListOf<CatcherAction>()
                    for (i in 1..catchers.size) {
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
                } else {
                    //generate only one
                    get().regex().getAllItems().find { it.key == "6digit" }?.let {
                        get().catcher().insert(
                            Catcher(
                                id = 1,
                                sender = "",
                                description = it.description,
                                regexId = it.id
                            )
                        )
                    }
                    get().action().getAllItems()
                        .filter { it.key == "tts" || it.key == "copy" || it.key == "notification" }
                        .map {
                            return@map CatcherAction(
                                catcherId = 1,
                                actionId = it.id,
                                params = it.defaultParams
                            )
                        }.let {
                        get().catcherAction().insertAll(*it.toTypedArray())
                    }
                }
            }

        }

    }


    private fun randomAction(n: Int, range: IntRange): List<Int> {

        val randomNumbers = mutableSetOf<Int>()
        while (randomNumbers.size < n) {
            randomNumbers.add(Random.nextInt(range.first, range.last + 1))
        }
        return randomNumbers.toList()
    }

    private fun generateVerificationCode(length: Int): String {
        return (1..length).map { Random.nextInt(0, 10) }.joinToString("")
    }

    private fun getRandomTemplateWithCode(smsTemplates: Map<String, String>): Triple<String, String, String> {
        val randomSender = smsTemplates.keys.random()
        val template = smsTemplates[randomSender] ?: "Template not found"

        // Generate random verification code
        val codeLength = template.count { it == 'X' }
        val verificationCode = generateVerificationCode(codeLength)

        // Replace Xs with the real code
        val formattedMessage = template.replace("X".repeat(codeLength), verificationCode)

        return Triple(randomSender, formattedMessage, verificationCode)
    }
}