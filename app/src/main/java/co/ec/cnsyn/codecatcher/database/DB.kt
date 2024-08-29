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
        return INSTANCE!!;
    }


    fun fillDatabase() {
        thread {

            val list = get().action().getAllItems()
            if (list.isEmpty()) {

                val regexes = regexList()
                get().regex().insertAll(*regexes.toTypedArray())
                get().action().insertAll(*actionList().toTypedArray())

                if (BuildConfig.DEBUG) {
                    //if debug generate some records to testing
                    val catchers = List(regexes.size) { it ->
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


                    generateFakeData()
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

    private fun generateFakeData() {
        val smsTemplates = mapOf(
            "BankAlert" to "Dear customer, your bank account has been updated. Your verification code is XXXX.",
            "CargoTrack" to "Your cargo is on the way! Track your shipment at https://tracking.example.com?code=XXXXX.",
            "OnlineShop" to "Thank you for your purchase! Your verification code is XXX. Visit our website for details.",
            "DeliveryInfo" to "Your delivery is scheduled for today. Verify with code XXXX at https://delivery.example.com.",
            "CreditAlert" to "Alert: A transaction has been made on your credit card. Your code is XXXXX.",
            "ServiceUpdate" to "Important update: Service changes will be implemented on [Date]. Use code XXXX for updates.",
            "AccountNoti" to "Notification: There has been a change in your account settings. Verify with code XXXXXX.",
            "PackageTrack" to "Your package is in transit! Track it here with: https://package.example.com/verify/XXXX",
            "OrderConfirm" to "Order Confirmation: Your order has been received. Verify with code XXXX.",
            "PromoAlert" to "Special Offer: Get 10% off with code XXXXX! Use it at checkout on our website.",
            "ServiceAlert" to "Service Alert: Scheduled maintenance on tomorrow. Use code XXX for more info on https://service.alert.com/user/XXX",
            "Transaction" to "Transaction Alert: A payment of 20$ has been processed. Verify with code XXXX.",
            "Appointment" to "Appointment Reminder: Your appointment on tomorrow at 15.00. Verify with code XXX.",
            "ShippingInfo" to "Shipping Information: Your order has been shipped. Track it here: https://shipping.world-info.com?code=XXXXX.",
            "BankNotification" to "Bank Notification: Your account balance changed. Verify with code XXXX.",
            "AlertService" to "Alert: Your account triggered an alert. Verify with code XXXXXX.",
            "ReceiptMsg" to "Receipt: Your recent transaction completed. Use code XXX for details in your account.",
            "DeliveryUpdate" to "Delivery Update: Your delivery status changed. Verify with code XXXX on https://update.adress-detail.com.",
            "SupportTeam" to "Support Team: We received your request. Verify with code XXXXXX. We'll get back to you shortly.",
        )
        val codes = mutableListOf<Code>()
        var newDate = 0
        var date = unix()
        val regexSize = regexList().size
        for (i in 1..150) {
            if (newDate == 0) {
                newDate = Random.nextInt(2, 6)
                date -= 86400
            }
            val (sender, message, code) = getRandomTemplateWithCode(smsTemplates)
            codes.add(
                Code(
                    date = date,
                    catcherId = Random.nextInt(1, regexSize + 1),
                    sender = sender,
                    sms = message,
                    code = code
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

    fun generateVerificationCode(length: Int): String {
        return (1..length).map { Random.nextInt(0, 10) }.joinToString("")
    }

    fun getRandomTemplateWithCode(smsTemplates: Map<String, String>): Triple<String, String, String> {
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