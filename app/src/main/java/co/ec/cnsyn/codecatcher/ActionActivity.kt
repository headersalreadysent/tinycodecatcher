package co.ec.cnsyn.codecatcher

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat.startActivity
import java.util.logging.Logger


class ActionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //why i need a another acitivty?
        //in some android versions multiple notification make mistake on destination extra
        //To eliminate this problem i use a second activity. maybe my mistake

        if (intent.action == "co.ec.cnsyn.codecatcher.DEBUG") {
            val component = ComponentName(this, DebugActivity::class.java)
            Log.d(
                "codecatcher",
                "debugenabled ${packageManager.getComponentEnabledSetting(component)}"
            )
            val state=packageManager.getComponentEnabledSetting(component)
            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
            ) {
                packageManager.setComponentEnabledSetting(
                    component,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )

            } else {
                packageManager.setComponentEnabledSetting(
                    component,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
            finish()
            return
        }


        val action = intent.getStringExtra("action") ?: "history"
        val code = intent.getStringExtra("code") ?: ""

        println("actionlog action:$action code:$code")
        when (action) {
            "copy" -> {

                val clipboard =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("code-catcher", code)
                clipboard.setPrimaryClip(clip)
                println("actionlog copy and stop")
                finish()
            }

            "history" -> redirectToHistory()
            else -> redirectToHistory()
        }


    }

    fun redirectToHistory() {
        val redirectIntent = Intent(this, MainActivity::class.java)
        redirectIntent.putExtra("destination", "history")
        startActivity(redirectIntent)
    }

}


