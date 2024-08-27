package co.ec.cnsyn.codecatcher.pages.history

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
import co.ec.cnsyn.codecatcher.helpers.dateString
import co.ec.cnsyn.codecatcher.helpers.unix
import kotlin.random.Random

open class HistoryViewModel : ViewModel() {

    var perPage=30
    var history = MutableLiveData<List<Pair<String, List<CodeDao.Latest>>>>(listOf())
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
        async({ DB.get().code().getLatest(perPage) }, {
            history.value =it.groupBy { it.code.date.dateString("MMM-YYYY") }.toList()
        })
    }

    fun loadMore() {
        val size = (history.value?.sumOf { it.second.size } ?: 0) + perPage
        async({ DB.get().code().getLatest(size) }, {
            history.value =it.groupBy { it.code.date.dateString("MMM-YYYY") }.toList()
        })
    }
}

class MockHistoryViewModel : HistoryViewModel() {
    override fun start() {

        history.value = List(40) {
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
        }.groupBy { it.code.date.dateString("MMM-YYYY") }.toList()

    }
}