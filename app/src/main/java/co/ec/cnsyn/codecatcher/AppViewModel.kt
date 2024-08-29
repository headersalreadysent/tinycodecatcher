package co.ec.cnsyn.codecatcher

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Notifications
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
import co.ec.cnsyn.codecatcher.helpers.EventBus
import co.ec.cnsyn.codecatcher.helpers.SmsCaught
import co.ec.cnsyn.codecatcher.helpers.async
import co.ec.cnsyn.codecatcher.helpers.translate
import co.ec.cnsyn.codecatcher.helpers.unix
import co.ec.cnsyn.codecatcher.pages.dashboard.DashboardViewModel
import co.ec.cnsyn.codecatcher.sms.ActionRunner
import co.ec.cnsyn.codecatcher.sms.SmsData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

open class AppViewModel : ViewModel() {


    var requiredPerms = MutableLiveData<List<PermissionInfo>>(listOf())

    var debug = MutableLiveData<Map<String, Any>>(mapOf())

    var debugRunning = false

    init {
        calculatePermissions()
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

    fun calculateDebugInfos() {
        debugRunning = true
        viewModelScope.launch {
            while (debugRunning) {
                calculateCrashDebug()
                calculateServiceDebug()
                delay(1000)
            }
        }
    }


    fun stopDebugCalculate() {
        debugRunning = false
    }


    private fun calculateServiceDebug() {
        async({ DB.get().service().getAllItems() }, {
            val old = debug.value?.toMutableMap() ?: mutableMapOf()
            old["service"] = it
            debug.value = old.toMap()
        })
    }

    private fun calculateCrashDebug() {
        val logs = ExceptionHandler.readExceptionLogs(App.context())
        val old = debug.value?.toMutableMap() ?: mutableMapOf()
        old["crash"] = logs
        debug.value = old.toMap()
    }


}
