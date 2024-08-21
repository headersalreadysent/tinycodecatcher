package co.ec.cnsyn.codecatcher

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import co.ec.cnsyn.codecatcher.helpers.MockSettings
import co.ec.cnsyn.codecatcher.helpers.Settings
import co.ec.cnsyn.codecatcher.helpers.unix
import co.ec.cnsyn.codecatcher.pages.about.About
import co.ec.cnsyn.codecatcher.pages.add.Add
import co.ec.cnsyn.codecatcher.pages.catcher.CatcherPage
import co.ec.cnsyn.codecatcher.pages.dashboard.Dashboard
import co.ec.cnsyn.codecatcher.pages.settings.SettingsModal
import co.ec.cnsyn.codecatcher.sms.DebugSmsReceiver
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DB.getDatabase(applicationContext)
        enableEdgeToEdge()
        setContent {
            CodeCatcherTheme {
                CodeCatcherApp(applicationContext)
            }
        }


        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(
                DebugSmsReceiver(),
                IntentFilter("co.ec.cnsyn.codecatcher.DEBUG_SMS"),
                RECEIVER_NOT_EXPORTED
            )
        }
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
    activity: Context,
    appModel: AppViewModel = viewModel()
) {
    val uiController = rememberSystemUiController()
    val surfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)

    val context = LocalContext.current
    val navController = rememberNavController()
    val db = DB.getDatabase(context)
    val snackbarHostState = SnackbarHostState()
    val settings = Settings(context)

    var permissionModel by remember { mutableStateOf(false) }
    val permissions by appModel.requiredPerms.observeAsState(listOf())
    LaunchedEffect(permissions) {
        val hiddenUntil = settings.getInt("permissionHidden", 0)
        if (hiddenUntil < unix() || hiddenUntil == 0) {
            permissionModel = permissions.isNotEmpty()
        }
    }
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


    CompositionLocalProvider(
        LocalNavigation provides navController,
        LocalDB provides db,
        LocalSnackbar provides snackbarHostState,
        LocalSettings provides settings
    ) {

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
                            Icon(Icons.Default.Dashboard, contentDescription = "")
                        }

                        IconButton(
                            onClick = {
                                navController.navigate("catchers")
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Icon(Icons.Default.Phishing, contentDescription = "")
                        }

                        IconButton(
                            onClick = {
                                settingsVisible = !settingsVisible
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "")
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
                                Icon(Icons.Default.Key, contentDescription = "")
                            }
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            navController.navigate("add")
                        }) {
                            Icon(Icons.Default.AddCircle, contentDescription = "")
                        }
                    },
                )
            }
        ) { _ ->
            NavHost(
                navController = navController,
                startDestination = "dashboard"
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
            }
        }


    }


    if (permissionModel) {
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
fun CodeCatcherPreview(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val db = DB.getDatabase(context)
    val snackbarHostState = SnackbarHostState()
    val settings = MockSettings(context)

    CompositionLocalProvider(
        LocalNavigation provides navController,
        LocalDB provides db,
        LocalSnackbar provides snackbarHostState,
        LocalSettings provides settings
    ) {
        CodeCatcherTheme {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    CodeCatcherTheme {
        CodeCatcherApp(App.context())
    }
}