package co.ec.cnsyn.codecatcher.helpers

import android.content.Context
import android.os.Handler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import co.ec.cnsyn.codecatcher.App
import kotlin.concurrent.thread


/**
 * run a job with background thread
 */
fun <T : Any?> async(
    run: () -> T,
    then: (res: T) -> Unit = { _ -> },
    err: (res: Throwable) -> Unit = { _ -> }
) {
    thread {
        //generate a thread
        //get main handler
        val mainHandler = Handler(App.context().mainLooper)
        try {
            //run method ang get result
            val result: T = run()
            mainHandler.post {
                //send to main thread with result
                then(result)
            }
        } catch (e: Throwable) {
            mainHandler.post {
                //send to main thread with error
                err(e)
            }
        }
    }
}

fun htmlToAnnotatedString(html: String, source: AnnotatedString.Builder? = null): AnnotatedString {
    val builder = source ?: AnnotatedString.Builder()

    var i = 0
    while (i < html.length) {
        when {
            html.startsWith("<b>", i) -> {
                i += 3 // Skip past "<b>"
                val end = html.indexOf("</b>", i)
                if (end != -1) {
                    builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(html.substring(i, end))
                    }
                    i = end + 4 // Skip past "</b>"
                }
            }

            html.startsWith("<i>", i) -> {
                i += 3 // Skip past "<i>"
                val end = html.indexOf("</i>", i)
                if (end != -1) {
                    builder.withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(html.substring(i, end))
                    }
                    i = end + 4 // Skip past "</i>"
                }
            }

            else -> {
                // Append normal text
                val nextTagStart = html.indexOf('<', i)
                if (nextTagStart == -1) {
                    builder.append(html.substring(i))
                    i = html.length
                } else {
                    builder.append(html.substring(i, nextTagStart))
                    i = nextTagStart
                }
            }
        }
    }

    return builder.toAnnotatedString()
}

fun translate(name: String, defText: String? = null): String {

    App.contextCheck()?.let {
        val resId: Int = it.resources.getIdentifier(name, "string", it.packageName)
        return if (resId != 0) {
            it.getString(resId)
        } else {
            defText ?: name
        }
    }
    return defText ?: name
}

@Composable
fun rememberKeyboardVisibility(): State<Boolean> {
    val isKeyboardVisible = remember { mutableStateOf(false) }
    val view = LocalView.current

    DisposableEffect(view) {
        val listener = ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            isKeyboardVisible.value = insets.isVisible(WindowInsetsCompat.Type.ime())
            insets
        }
        onDispose {
            listener.let { ViewCompat.setOnApplyWindowInsetsListener(view, null) }
        }
    }

    return isKeyboardVisible
}