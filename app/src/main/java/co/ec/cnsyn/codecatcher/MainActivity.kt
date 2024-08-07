package co.ec.cnsyn.codecatcher

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.ec.cnsyn.codecatcher.database.AppDatabase
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.pages.dashboard.Dashboard
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme


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
    }
}


val LocalDB = compositionLocalOf<AppDatabase> { error("No NavController provided") }
val LocalNavigation = compositionLocalOf<NavHostController> { error("No NavController provided") }

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CodeCatcherApp(context: Context) {

    val navController = rememberNavController()
    val db = DB.getDatabase(context)
    CompositionLocalProvider(
        LocalNavigation provides navController,
        LocalDB provides db
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            bottomBar = {
                BottomAppBar(
                    actions = {
                        IconButton(
                            onClick = { /*TODO*/ },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Icon(Icons.Default.Home, contentDescription = "")
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Default.AddCircle, contentDescription = "")
                        }
                    },
                    tonalElevation = 4.dp
                )
            }
        ) { _ ->
            NavHost(navController = navController, startDestination = "dashboard") {
                composable("dashboard") { Dashboard() }
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