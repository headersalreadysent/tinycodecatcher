package co.ec.cnsyn.codecatcher.pages.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ec.cnsyn.codecatcher.App
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.action.Action
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.catcheraction.CatcherAction
import co.ec.cnsyn.codecatcher.database.code.Code
import co.ec.cnsyn.codecatcher.database.code.CodeDao
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.helpers.async
import co.ec.cnsyn.codecatcher.helpers.translate
import co.ec.cnsyn.codecatcher.helpers.unix
import co.ec.cnsyn.codecatcher.helpers.EventBus
import co.ec.cnsyn.codecatcher.helpers.SmsCaught
import kotlinx.coroutines.launch
import kotlin.random.Random

open class DashboardViewModel : ViewModel() {


    var stats = MutableLiveData(mapOf("catcher" to 0, "code" to 0))
    var codes = MutableLiveData<List<CodeDao.Latest>>(listOf())
    var calendar = MutableLiveData<List<Int>>(listOf())
    var requiredPerms = MutableLiveData<List<PermissionInfo>>(listOf())


    init {
        start()
        viewModelScope.launch {
            EventBus.subscribe<SmsCaught> { message ->
                loadLatest()
            }
        }
    }


    open fun start() {

        //load stats
        loadLatest()
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
        })
        calculatePermissions()
    }

    /**
     * load latest codes
     */
    private fun loadLatest() {
        async({ DB.get().code().getLatest() }, {
            codes.value = it
        })
    }


    data class PermissionInfo(val permission: String, val icon: ImageVector, val text: String)

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
                    Icons.Filled.Message,
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
                    Icons.Filled.Send,
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
    }
}