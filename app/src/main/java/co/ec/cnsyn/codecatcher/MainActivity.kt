package co.ec.cnsyn.codecatcher

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.ec.cnsyn.codecatcher.database.AppDatabase
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.helpers.Settings
import co.ec.cnsyn.codecatcher.pages.add.Add
import co.ec.cnsyn.codecatcher.pages.catcher.CatcherPage
import co.ec.cnsyn.codecatcher.pages.dashboard.Dashboard
import co.ec.cnsyn.codecatcher.pages.settings.SettingsModal
import co.ec.cnsyn.codecatcher.sms.DebugSmsReceiver
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme
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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(
                DebugSmsReceiver(),
                IntentFilter("co.ec.cnsyn.codecatcher.DEBUG_SMS"),
                RECEIVER_NOT_EXPORTED
            )
        }
    }
}


val LocalDB = compositionLocalOf<AppDatabase> { error("No NavController provided") }
val LocalNavigation = compositionLocalOf<NavHostController> { error("No NavController provided") }
val LocalSnackbar = compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }
val LocalSettings = compositionLocalOf<Settings> { error("No LocalSettings provided") }

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CodeCatcherApp(context: Context) {
    val uiController = rememberSystemUiController()
    val surfaceColor=MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    SideEffect {
        uiController.setNavigationBarColor(
            color = surfaceColor,
            darkIcons = true
        )
    }

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
                            Icon(Icons.Default.Home, contentDescription = "")
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
            }
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