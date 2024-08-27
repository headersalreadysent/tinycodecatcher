package co.ec.cnsyn.codecatcher

import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.ui.theme.CodeCatcherTheme


class NotificationActivity : ComponentActivity() {

    companion object {
        var isLoading: Boolean = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val destination = intent.getStringExtra("destination") ?: "dashboard"
        val destinationParam = intent.getStringExtra("destinationParam") ?: ""

        println("start activity destination $destination")

        DB.getDatabase(applicationContext)
        enableEdgeToEdge()
        setContent {
            CodeCatcherTheme {
                CodeCatcherApp(
                    startDestination = destination,
                    destinationParam = destinationParam
                )
            }
        }
        installSplashScreen().setKeepOnScreenCondition {
            return@setKeepOnScreenCondition isLoading
        }
        Handler(App.context().mainLooper).postDelayed({
            isLoading = false
        }, 3000)

    }
}
