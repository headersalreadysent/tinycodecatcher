package co.ec.cnsyn.codecatcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.servicelog.ServiceLog
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme


class DebugActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        DB.getDatabase(applicationContext)
        enableEdgeToEdge()
        setContent {
            CodeCatcherTheme {
                CodeCatcherDebug()
            }
        }

    }
}

@Composable
fun CodeCatcherDebug(
    model: AppViewModel = viewModel()
) {
    val tabs = listOf("Crash", "Service","Service Day","App Logs")
    var selectedTabIndex by remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        model.calculateDebugInfos()
        onDispose {
            model.stopDebugCalculate()
        }
    }
    Scaffold(
        topBar = {
            TabRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                selectedTabIndex = selectedTabIndex
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
        },
        floatingActionButton = {
            val context= LocalContext.current
            FloatingActionButton(onClick = {
                val debugActivity = Intent(context, MainActivity::class.java)
                context.startActivity(debugActivity)
            }) {
                Icon(Icons.Filled.Replay, contentDescription = "")
            }
        }
    ) { _ ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 85.dp)
        ) {
            when (selectedTabIndex) {
                0 -> CrashDebug(model)
                1 -> ServiceDebug(model)
                2 -> ServiceDebug(model,true)
                3 -> AppLog(model)
                else -> Text(text = "not-found")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CodeCatcherDebugPreview() {
    CodeCatcherPreview {
        CodeCatcherDebug()
    }
}


@Composable
fun ServiceDebug(model: AppViewModel,forDay:Boolean=false) {

    val debug by model.debug.observeAsState(mapOf())
    if (debug.keys.contains("service"))
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            var services = debug["service"]
            if (services is List<*>) {
                services = services.reversed()
                if(forDay){
                    var map= mutableMapOf<String,ServiceLog>()
                    services.forEach { it ->
                        var service=it as ServiceLog
                        var date=service.date.substring(0..10)
                        if(map.keys.contains(date)){
                            map[date]=ServiceLog(
                                date=date,
                                receiverId = map[date]?.receiverId+"\n"+service.receiverId,
                                heartbeatCount = (map[date]?.heartbeatCount?:0)+service.heartbeatCount,
                            )
                        } else {
                            map[date]=service
                        }
                    }
                    services=map.values.toList()
                }
                services as List<*>
                items(services.size) {
                    var serviceLog = services[it] as ServiceLog
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        headlineContent = {
                            Text(
                                text = serviceLog.date,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        supportingContent = {
                            Text(
                                text = serviceLog.receiverId,
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        trailingContent = {
                            Text(
                                text = serviceLog.heartbeatCount.toString(),
                                style = MaterialTheme.typography.bodyLarge
                                    .copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                            )
                        }
                    )
                }
            }

        }

}

@Composable
fun AppLog(model: AppViewModel) {

    val debug by model.debug.observeAsState(mapOf())
    if (debug.keys.contains("service"))
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            var messages = debug["applog"]
            if (messages is List<*>) {
                items(messages.size) {
                    val logItem = messages[it] as String
                    var parts=logItem.split("#")
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        headlineContent = {
                            if(parts.size==1){
                                Text(
                                    text = logItem,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(
                                        text = parts[0],
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = .8F),
                                            fontSize = 10.sp
                                        )
                                    )
                                    Text(
                                        text = parts[1],
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = .8F),
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        },
                        supportingContent = {
                            if(parts.size>1){
                                Text(
                                    text = parts[2],
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        },
                    )
                }
            }

        }

}



@Composable
fun CrashDebug(model: AppViewModel) {

    val debug by model.debug.observeAsState(mapOf())
    if (debug.keys.contains("crash"))
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            val crashes = debug["crash"]
            if (crashes is List<*>) {
                if (crashes.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                            Text(
                                text = "No Crash Yet",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    item {
                        val context = LocalContext.current
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                ExceptionHandler.clearCrashLogs(context)
                            }) {
                            Text(text = "Clear crashes")
                        }
                    }
                }
                items(crashes.size) {
                    val crash = crashes[it] as Pair<*, *>
                    var visible by remember { mutableStateOf(false) }
                    if (visible) {
                        Dialog(onDismissRequest = {
                            visible = false
                        }, content = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(.9F)
                                    .background(Color.White)
                                    .verticalScroll(rememberScrollState())
                                    .padding(8.dp),
                            ) {
                                SelectionContainer {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp),
                                        text = crash.second.toString(),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            textAlign = TextAlign.Justify
                                        )
                                    )
                                }
                            }
                        })
                    }
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .clickable {
                                visible = true
                            },
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        headlineContent = {
                            Text(
                                text = crash.first.toString(),
                                style = MaterialTheme.typography.bodyMedium,

                                )
                        },
                    )
                }
            }

        }

}