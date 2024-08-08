package co.ec.cnsyn.codecatcher.helpers

import android.os.Handler
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

