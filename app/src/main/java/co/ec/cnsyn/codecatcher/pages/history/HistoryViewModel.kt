package co.ec.cnsyn.codecatcher.pages.history

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.action.Action
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.catcheraction.CatcherAction
import co.ec.cnsyn.codecatcher.database.code.Code
import co.ec.cnsyn.codecatcher.database.code.CodeDao
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.helpers.async
import co.ec.cnsyn.codecatcher.helpers.unix
import kotlin.random.Random

open class HistoryViewModel : ViewModel() {


    var history = MutableLiveData<List<CodeDao.Latest>>(listOf())
    var count = MutableLiveData(0)

    init {
        start()
    }

    open fun start() {

        //load stats
        loadLatest()

    }

    private fun loadLatest() {
        async({ DB.get().code().getCount() }, {
            count.value = it
        })
        async({ DB.get().code().getLatest(20) }, {
            history.value = it
        })
    }

    fun loadMore(then: (res: Int) -> Unit = { _ -> }, err: (res: Throwable) -> Unit = { _ -> }) {
        val size = (history.value?.size ?: 0) + 20
        async({ DB.get().code().getLatest(size) }, {
            history.value = it
        })
    }
}

class MockHistoryViewModel : HistoryViewModel() {
    override fun start() {

        history.value = List(20) {
            var code = Random.nextInt(10000, 90000).toString()
            CodeDao.Latest(
                code = Code(
                    date = unix() - (it * 86400),
                    catcherId = Random.nextInt(1, 6),
                    sender = "Sender${Random.nextInt(1, 10)}",
                    sms = "Sample SMS text $code",
                    code = code
                ),
                catcher = Catcher(
                    id = it + 1,
                    sender = "Random Sender $it",
                    description = "",
                    regexId = 1
                ),
                actions = listOf(
                    ActionDetail(
                        action = CatcherAction(catcherId = 1, actionId = 1),
                        detail = Action(id = 1, icon = "ContentCopy"),
                    ),

                    ActionDetail(
                        action = CatcherAction(catcherId = 1, actionId = 2),
                        detail = Action(id = 2, icon = "Mic"),
                    )
                )
            )
        }

    }
}