package co.ec.cnsyn.codecatcher.pages.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.action.Action
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.catcheraction.CatcherAction
import co.ec.cnsyn.codecatcher.database.code.Code
import co.ec.cnsyn.codecatcher.database.code.CodeDao
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.database.relations.CodeWithCatcher
import co.ec.cnsyn.codecatcher.helpers.async
import co.ec.cnsyn.codecatcher.helpers.unix

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.random.Random

open class DashboardViewModel : ViewModel() {


    var stats = MutableLiveData(mapOf("catcher" to 0, "code" to 0))

    var codes = MutableLiveData<List<CodeDao.Latest>>(listOf())

    init {
        start()
    }

    open fun start() {
        //load stats
        async({
            val catcherCount = DB.get().catcher().getActiveCount()
            val codeCount = DB.get().code().getCount()
            return@async mapOf(
                "catcher" to catcherCount,
                "code" to codeCount
            )
        }, {
            stats.value = it
        })
        async({

        })
            DB.get().code().getLatest({
                codes.value=it
            })

    }
}

class MockDashboardViewModel : DashboardViewModel() {

    override fun start() {
        stats.value =
            mapOf(
                "catcher" to Random.nextInt(1, 4),
                "code" to Random.nextInt(589, 795)
            )
        codes.value = List(20) {
            var code=Random.nextInt(10000, 90000).toString()
            CodeDao.Latest(
                code = Code(
                    date = unix() - (it*86400),
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