package co.ec.cnsyn.codecatcher

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity


class ActionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //why i need a another acitivty?
        //in some android versions multiple notification make mistake on destination extra
        //To eliminate this problem i use a second activity. maybe my mistake

        val action = intent.getStringExtra("action") ?: "redirect"
        val code = intent.getStringExtra("code") ?: ""

        println("actionlog action:$action code:$code")
        when(action){
            "copy" -> {

                val clipboard =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("code-catcher", code)
                clipboard.setPrimaryClip(clip)
                println("actionlog copy and stop")
                finish()
            }
            "redirect"-> redirectToHistory()
            else -> redirectToHistory()
        }


    }

    fun redirectToHistory(){
      /*  val redirectIntent = Intent(this, MainActivity::class.java)
        redirectIntent.putExtra("destination","history")
        startActivity(redirectIntent)*/
    }
}


