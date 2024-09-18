package co.ec.cnsyn.codecatcher

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.ec.cnsyn.codecatcher.composables.RealDevice
import co.ec.cnsyn.codecatcher.composables.SkewBottomSheet
import co.ec.cnsyn.codecatcher.composables.SkewSquareCut
import co.ec.cnsyn.codecatcher.database.AppDatabase
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.helpers.AppLogger
import co.ec.cnsyn.codecatcher.helpers.MockSettings
import co.ec.cnsyn.codecatcher.helpers.Settings
import co.ec.cnsyn.codecatcher.helpers.unix
import co.ec.cnsyn.codecatcher.pages.about.About
import co.ec.cnsyn.codecatcher.pages.add.Add
import co.ec.cnsyn.codecatcher.pages.catcher.CatcherPage
import co.ec.cnsyn.codecatcher.pages.dashboard.Dashboard
import co.ec.cnsyn.codecatcher.pages.help.Help
import co.ec.cnsyn.codecatcher.pages.history.History
import co.ec.cnsyn.codecatcher.pages.settings.SettingsModal
import co.ec.cnsyn.codecatcher.sms.SmsService
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController


class MainActivity : ComponentActivity() {

    companion object {
        val handler = Handler(App.context().mainLooper)
        var isLoading: Boolean = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val destination = intent.getStringExtra("destination") ?: "dashboard"
        val destinationParam = intent.getStringExtra("destinationParam") ?: ""

        AppLogger.d("start activity destination $destination")

        DB.getDatabase(applicationContext)
        enableEdgeToEdge()
        setContent {
            CodeCatcherTheme {
                CodeCatcherApp(
                    startDestination = destination,
                    destinationParam = destinationParam
                )
                HorizontalDivider(
                    modifier = Modifier.height(0.dp),
                    color = Color.Transparent,
                    thickness = 0.dp
                )
            }
        }
        installSplashScreen().setKeepOnScreenCondition {
            return@setKeepOnScreenCondition isLoading
        }
        handler.postDelayed({
            isLoading = false
        }, 3000)

        handler.postDelayed({
            //after 5 second make it restart 0
            Settings(this).putInt("appRestartAfterError", 0)
        }, 5000L)
        handler.postDelayed({
            //start after 5 second
            SmsService.setupService(applicationContext)
        }, 5000L)


    }
}


val LocalDB = compositionLocalOf<AppDatabase> { error("No DB provided") }
val LocalNavigation = compositionLocalOf<NavHostController> { error("No navcontroller provided") }
val LocalSnackbar = compositionLocalOf<SnackbarHostState> { error("No snackbarhost provided") }
val LocalSettings = compositionLocalOf<Settings> { error("No settings provided") }

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CodeCatcherApp(
    startDestination: String = "dashboard",
    destinationParam: String = "",
    appModel: AppViewModel = viewModel()
) {
    val uiController = rememberSystemUiController()
    val surfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    SideEffect {
        uiController.setNavigationBarColor(
            color = surfaceColor,
            darkIcons = ColorUtils.calculateLuminance(surfaceColor.toArgb()) > 0.5
        )
        uiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = ColorUtils.calculateLuminance(surfaceColor.toArgb()) > 0.5
        )
    }
    CodeCatcherProviders {
        var permissionModel by remember { mutableStateOf(false) }
        val permissions by appModel.requiredPerms.observeAsState(listOf())

        val navController = LocalNavigation.current
        val snackbarHostState = LocalSnackbar.current
        val settings = LocalSettings.current

        LaunchedEffect(permissions) {
            if (permissions.isEmpty()) {
                permissionModel = false
            } else {
                val hiddenUntil = settings.getInt("permissionHidden", 0)
                if (hiddenUntil < unix() || hiddenUntil == 0) {
                    permissionModel = permissions.isNotEmpty()
                }
            }
        }


        var settingsVisible by remember { mutableStateOf(false) }
        if (settingsVisible) {
            SettingsModal {
                settingsVisible = false
            }
        }
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            bottomBar = {
                BottomAppBar(
                    tonalElevation = 3.dp,
                    actions = {
                        IconButton(
                            onClick = {
                                navController.navigate("dashboard")
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Icon(Icons.Default.Dashboard, contentDescription = "dashboard")
                        }

                        IconButton(
                            onClick = {
                                navController.navigate("catchers")
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Icon(Icons.Default.Phishing, contentDescription = "catchers page")
                        }

                        IconButton(
                            onClick = {
                                settingsVisible = !settingsVisible
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "toggle settings")
                        }
                        if (permissions.isNotEmpty() && !permissionModel) {
                            IconButton(
                                onClick = {
                                    permissionModel = !permissionModel
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Icon(Icons.Default.Key, contentDescription = "permission screen")
                            }
                        }
                        if (settings.getBoolean("debug-enabled", false)) {
                            val context = LocalContext.current
                            IconButton(
                                onClick = {
                                    val debugActivity = Intent(context, DebugActivity::class.java)
                                    context.startActivity(debugActivity)
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Icon(Icons.Filled.DeveloperMode, contentDescription = "debug screen")
                            }
                        }

                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            navController.navigate("add")
                        }) {
                            Icon(Icons.Default.AddCircle, contentDescription = "add button")
                        }
                    },
                )
            }
        ) { _ ->

            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable("dashboard") { Dashboard() }
                composable(
                    "catchers/{catcherId}",
                    arguments = listOf(navArgument("catcherId") { nullable = true })
                ) { backStackEntry ->
                    val catcherId = backStackEntry.arguments?.getString("catcherId")?.toInt()
                    CatcherPage(catcherId = catcherId)
                }
                composable("catchers") { CatcherPage() }
                composable("add") { Add() }
                composable("about") { About() }
                composable("history") { History() }
                composable("help") { Help(helpType = destinationParam) }
            }
        }

        if (permissionModel && permissions.isNotEmpty()) {
            SkewBottomSheet(onDismissRequest = {
                permissionModel = false
                settings.putInt("permissionHidden", unix().toInt() + 43200)
            }, cut = SkewSquareCut.TopStart) {
                PermissionArea(permission = permissions) {
                    appModel.calculatePermissions()
                }
            }
        }

    }


}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionArea(
    permission: List<AppViewModel.PermissionInfo>,
    then: (perm: String) -> Unit = { _ -> }
) {

    RealDevice {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = stringResource(id = R.string.dashboard_permission_needs_some_permission),
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                items(permission.size) {
                    val perm = permission[it]
                    val permState = rememberPermissionState(
                        permission = perm.permission,
                        onPermissionResult = {
                            then(perm.permission)
                        }
                    )
                    Button(
                        onClick = {
                            permState.launchPermissionRequest()
                        },
                        contentPadding = PaddingValues(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                        modifier = Modifier.padding(start = 4.dp),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                perm.icon, contentDescription = perm.permission,
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        CircleShape
                                    )
                                    .scale(.8F),
                                tint = MaterialTheme.colorScheme.primary

                            )
                            Text(
                                text = perm.text,
                                modifier = Modifier.padding(start = 5.dp),
                                maxLines = 2
                            )

                        }
                    }
                }


            }
        }

    }


}

@Composable
fun CodeCatcherProviders(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val db = DB.getDatabase(context)
    val snackbarHostState = SnackbarHostState()
    val settings = Settings(context)
    CompositionLocalProvider(
        LocalNavigation provides navController,
        LocalDB provides db,
        LocalSnackbar provides snackbarHostState,
        LocalSettings provides settings
    ) {
        content()
    }
}

@Composable
fun CodeCatcherPreview(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val settings = MockSettings(context)
    CodeCatcherProviders {


        CompositionLocalProvider(
            LocalSettings provides settings
        ) {
            CodeCatcherTheme {
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    CodeCatcherTheme {
        CodeCatcherApp()
    }
}