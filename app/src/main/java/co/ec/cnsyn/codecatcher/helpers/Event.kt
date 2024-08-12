package co.ec.cnsyn.codecatcher.helpers

import co.ec.cnsyn.codecatcher.sms.SmsData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class GlobalEvent {
    data class SmsReceived(var smsData:SmsData) : GlobalEvent()
}
object Event {
    private val _events = MutableSharedFlow<GlobalEvent>(replay = 0)
    val events: SharedFlow<GlobalEvent> = _events.asSharedFlow()

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun emit(event: GlobalEvent) {
        scope.launch {
            _events.emit(event)
        }
    }
}