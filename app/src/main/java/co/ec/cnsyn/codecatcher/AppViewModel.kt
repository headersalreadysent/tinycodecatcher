package co.ec.cnsyn.codecatcher

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.helpers.AppLogger
import co.ec.cnsyn.codecatcher.helpers.CatcherSaved
import co.ec.cnsyn.codecatcher.helpers.EventBus
import co.ec.cnsyn.codecatcher.helpers.Settings
import co.ec.cnsyn.codecatcher.helpers.async
import co.ec.cnsyn.codecatcher.helpers.translate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class AppViewModel : ViewModel() {


    var requiredPerms = MutableLiveData<List<PermissionInfo>>(listOf())

    var debug = MutableLiveData<Map<String, Any>>(mapOf())

    private var debugRunning = false


    init {
        viewModelScope.launch {
            EventBus.subscribe<CatcherSaved> {
                calculatePermissions()
            }
            delay(3000)
            calculatePermissions()
        }
    }


    data class PermissionInfo(
        val permission: String?,
        val icon: ImageVector,
        val text: String,
        val detail: String,
        val click: (() -> Unit)? = null
    )


    /**
     * calculate permission list
     */
    fun calculatePermissions() {
        val permissions = mutableListOf<PermissionInfo>()
        val context = App.context()
        if (context.checkSelfPermission(Manifest.permission.RECEIVE_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            //receive sms permission
            permissions.add(
                PermissionInfo(
                    Manifest.permission.RECEIVE_SMS,
                    Icons.AutoMirrored.Filled.Message,
                    translate("dashboard_permission_RECEIVE_SMS"),
                    translate("dashboard_permission_RECEIVE_SMS_help")
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
                    translate("dashboard_permission_POST_NOTIFICATIONS"),
                    translate("dashboard_permission_POST_NOTIFICATIONS_help")
                )
            )
        }


        async({
            return@async DB.get().service().totalRunTime()
        }, {
            val settings = Settings.get()
            val diff = settings.getInt("lastStart", 0) - settings.getInt("firstStart", 0)
            if (diff > 86400 * 5 && it < diff * .8F) {
                //if five day old app and run is less then 4 day
                val alarmManager =
                    App.context().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    !alarmManager.canScheduleExactAlarms()
                ) {
                    val perms = requiredPerms.value?.toMutableList() ?: mutableListOf()
                    val isAdded = perms.find { it.permission == Manifest.permission.SEND_SMS }
                    if (isAdded == null) {
                        //send sms permission
                        perms.add(
                            PermissionInfo(
                                null,
                                Icons.Filled.Alarm,
                                translate("dashboard_permission_SET_ALARM"),
                                translate("dashboard_permission_SET_ALARM_help")
                            ) {
                                val scheduleIntent = Intent().apply {
                                    action =
                                        android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                                    data = android.net.Uri.parse("package:${context.packageName}")
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(scheduleIntent)
                            }
                        )
                    }
                    requiredPerms.value = perms
                }
            }
        })

        requiredPerms.value = permissions.toList()
        async({
            return@async DB.get().action().hasSmsAction()
        }, {
            if (it > 0) {
                //has sms action lets ask sms send
                if (context.checkSelfPermission(Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    val perms = requiredPerms.value?.toMutableList() ?: mutableListOf()
                    val isAdded = perms.find { it.permission == Manifest.permission.SEND_SMS }
                    if (isAdded == null) {
                        //send sms permission
                        perms.add(
                            PermissionInfo(
                                Manifest.permission.SEND_SMS,
                                Icons.AutoMirrored.Filled.Send,
                                translate("dashboard_permission_SEND_SMS"),
                                translate("dashboard_permission_SEND_SMS_help")
                            )
                        )
                    }
                    requiredPerms.value = perms
                }
            }
        })


    }

    fun calculateDebugInfos() {
        debugRunning = true
        viewModelScope.launch {
            while (debugRunning) {
                calculateCrashDebug()
                calculateServiceDebug()
                calculateAppLogDebug()
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

    private fun calculateAppLogDebug() {
        val logs = ExceptionHandler.readAppLogs(App.context())
        val old = debug.value?.toMutableMap() ?: mutableMapOf()
        old["applog"] = logs
        debug.value = old.toMap()
    }

    fun clearServiceRecords() {

        async({ DB.get().service().clean() }, {

        })
    }


}
