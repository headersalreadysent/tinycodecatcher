package co.ec.cnsyn.codecatcher.values

import co.ec.cnsyn.codecatcher.sms.SmsData
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlin.coroutines.coroutineContext

object EventBus {
    private val _events = MutableSharedFlow<Any>()
    val events = _events.asSharedFlow()

    suspend fun publish(event: Any) {
        _events.emit(event)
    }

    suspend inline fun <reified T> subscribe(crossinline onEvent: (T) -> Unit) {
        events.filterIsInstance<T>()
            .collectLatest { event ->
                coroutineContext.ensureActive()
                onEvent(event)
            }
    }
}

data class SmsCauched(var sms: SmsData)