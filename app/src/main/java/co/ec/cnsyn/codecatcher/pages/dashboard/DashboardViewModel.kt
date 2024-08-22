package co.ec.cnsyn.codecatcher.pages.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ec.cnsyn.codecatcher.App
import co.ec.cnsyn.codecatcher.MainActivity
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.action.Action
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.catcheraction.CatcherAction
import co.ec.cnsyn.codecatcher.database.code.Code
import co.ec.cnsyn.codecatcher.database.code.CodeDao
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.helpers.EventBus
import co.ec.cnsyn.codecatcher.helpers.SmsCaught
import co.ec.cnsyn.codecatcher.helpers.async
import co.ec.cnsyn.codecatcher.helpers.translate
import co.ec.cnsyn.codecatcher.helpers.unix
import co.ec.cnsyn.codecatcher.sms.ActionRunner
import co.ec.cnsyn.codecatcher.sms.SmsData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

open class DashboardViewModel : ViewModel() {

    var loadStep = MutableLiveData<Int>(0)

    var stats = MutableLiveData(mapOf("catcher" to 0, "code" to 0))
    var codes = MutableLiveData<List<CodeDao.Latest>>(listOf())
    var calendar = MutableLiveData<List<Int>>(listOf())
    var requiredPerms = MutableLiveData<List<PermissionInfo>>(listOf())


    var graphStat = MutableLiveData<Map<String, List<Pair<String, Float>>>>(mapOf())


    init {
        start()
        viewModelScope.launch {
            EventBus.subscribe<SmsCaught> { _ ->
                loadLatest()
                calculateCatcherStats()
            }
        }
        loadStep.observeForever {
            if ((loadStep.value ?: 0) > 4) {
                viewModelScope.launch {
                    delay(1500)
                    MainActivity.isLoading = false
                }
            }
        }
    }


    open fun start() {

        //load stats
        loadLatest()
        calculateCatcherStats()
        async({
            val catcherCount = DB.get().catcher().getActiveCount()
            val codeCount = DB.get().code().getCount()
            return@async mapOf(
                "catcher" to catcherCount,
                "code" to codeCount
            )
        }, {
            stats.value = it
        })
        async({ DB.get().code().getCalendar(unix() - 86400 * 30) }, {
            calendar.value = it.map { it.date }

            loadStep.value = (loadStep.value ?: 0) + 1
        })
        calculatePermissions()

    }

    /**
     * load latest codes
     */
    private fun loadLatest() {
        async({ DB.get().code().getLatest() }, {
            codes.value = it
            loadStep.value = (loadStep.value ?: 0) + 1
        })
    }

    /**
     * calculate catcher stats
     */
    private fun calculateCatcherStats() {
        async({ DB.get().code().catcherCountStats() }, {
            if (it.isNotEmpty()) {
                val mutable = (graphStat.value ?: mapOf()).toMutableMap()
                mutable["catcher"] = it.map {
                    return@map Pair(
                        (if (it.sender != "") it.sender + "-" else "") + it.name,
                        it.count.toFloat()
                    )
                }
                graphStat.value = mutable.toMap()

                loadStep.value = (loadStep.value ?: 0) + 1
            }
        })
        async({ DB.get().code().senderCountStat() }, {
            if (it.isNotEmpty()) {
                val mutable = (graphStat.value ?: mapOf()).toMutableMap()
                mutable["sender"] = it.map {
                    return@map Pair(it.name, it.count.toFloat())
                }
                graphStat.value = mutable.toMap()

                loadStep.value = (loadStep.value ?: 0) + 1
            }
        })
        async({ DB.get().action().actionCountStats() }, {
            if (it.isNotEmpty()) {
                val mutable = (graphStat.value ?: mapOf()).toMutableMap()
                mutable["action"] = it.map {
                    return@map Pair(it.name, it.count.toFloat())
                }
                graphStat.value = mutable.toMap()

                loadStep.value = (loadStep.value ?: 0) + 1
            }
        })
    }


    data class PermissionInfo(val permission: String, val icon: ImageVector, val text: String)

    fun generateTestSms() {
        val testSender = translate("dashboard_test_sms_sender")
        val testContent =
            translate("dashboard_test_sms_content") + " " + Random.nextInt(100000, 999999)
        ActionRunner().runSmsList(
            listOf(
                SmsData(testSender, testContent, unix())
            )
        )
        start()
    }

    /**
     * calculate permission list
     */
    fun calculatePermissions() {
        val permissions = mutableListOf<PermissionInfo>();
        val context = App.context()
        if (context.checkSelfPermission(Manifest.permission.RECEIVE_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            //receive sms permission
            permissions.add(
                PermissionInfo(
                    Manifest.permission.RECEIVE_SMS,
                    Icons.AutoMirrored.Filled.Message,
                    translate("dashboard_permission_RECEIVE_SMS")
                )
            )
        }
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            //post notification permission
            permissions.add(
                PermissionInfo(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Icons.Default.Notifications,
                    translate("dashboard_permission_POST_NOTIFICATIONS")
                )
            )
        }
        if (context.checkSelfPermission(Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            //send sms permission
            permissions.add(
                PermissionInfo(
                    Manifest.permission.SEND_SMS,
                    Icons.AutoMirrored.Filled.Send,
                    translate("dashboard_permission_SEND_SMS")
                )
            )
        }
        requiredPerms.value = permissions.toList()

    }


}

class MockDashboardViewModel : DashboardViewModel() {

    override fun start() {
        stats.value =
            mapOf(
                "catcher" to Random.nextInt(1, 4),
                "code" to Random.nextInt(589, 795)
            )
        codes.value = List(20) {
            var code = Random.nextInt(10000, 90000).toString()
            CodeDao.Latest(
                code = Code(
                    date = unix() - (it * 86400),
                    catcherId = Random.nextInt(1, 6),
                    sender = "Sender${Random.nextInt(1, 10)}",
                    sms = "Sample SMS text $code",
                    code = code
                ),
                catcher = Catcher(
                    id = it + 1,
                    sender = "Random Sender $it",
                    description = "",
                    regexId = 1
                ),
                actions = listOf(
                    ActionDetail(
                        action = CatcherAction(catcherId = 1, actionId = 1),
                        detail = Action(id = 1, icon = "ContentCopy"),
                    ),

                    ActionDetail(
                        action = CatcherAction(catcherId = 1, actionId = 2),
                        detail = Action(id = 2, icon = "Mic"),
                    )
                )
            )
        }
        var now = unix()
        calendar.value = List(30) {
            now = (now - 86400 * Random.nextFloat()).toLong()
            return@List now.toInt()
        }
        graphStat.value = mapOf(
            "catcher" to List(Random.nextInt(3, 7)) {
                Pair("Catcher $it", Random.nextFloat() * 10)
            },
            "action" to List(Random.nextInt(3, 7)) {
                Pair("Action $it", Random.nextFloat() * 10)
            },
            "sender" to List(Random.nextInt(3, 7)) {
                Pair("Sender $it", Random.nextFloat() * 10)
            }
        )
    }
}