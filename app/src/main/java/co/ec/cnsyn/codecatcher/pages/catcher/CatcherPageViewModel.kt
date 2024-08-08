package co.ec.cnsyn.codecatcher.pages.catcher


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.action.Action
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.catcher.CatcherDao
import co.ec.cnsyn.codecatcher.database.catcher.CatcherDao.CatcherDetail
import co.ec.cnsyn.codecatcher.database.catcheraction.CatcherAction
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.helpers.async
import co.ec.cnsyn.codecatcher.values.actionList
import co.ec.cnsyn.codecatcher.values.regexList
import kotlinx.coroutines.launch

open class CatcherPageViewModel : ViewModel() {

    var catchers = MutableLiveData<List<CatcherDao.CatcherDetail>>(listOf())

    var allActions = MutableLiveData<List<Action>>(listOf())

    init {
        start()
    }

    open fun start() {
        //load stats
        DB.get().catcher().getCatchersWithDetails({
            catchers.value = it
        }, { err ->
            println(err)
        })
        async({
            return@async DB.get().action().getAllItems()
        }, {
            allActions.value = it
        })


    }

    fun actionStatus(catcherDetail: CatcherDetail, action: Action, targetStatus: Boolean = true) {

        async({
            if (!targetStatus) {
                return@async DB.get().catcherAction().disable(catcherDetail.catcher.id, action.id)
            } else {
                var catcherAction =
                    DB.get().catcherAction().getOne(catcherDetail.catcher.id, action.id)
                if (catcherAction == null) {
                    catcherAction = CatcherAction(
                        catcherId = catcherDetail.catcher.id,
                        actionId = action.id,
                        params = "",
                        status = 1
                    )
                }
                catcherAction.status = 1
                return@async DB.get().catcherAction().insert(catcherAction)
            }
        }, {
            reloadOneCatcher(catcherDetail.catcher.id)
        })

    }


    /**
     * reload catcher details
     */
    private fun reloadOneCatcher(catcherId: Int, then: () -> Unit = { }) {

        DB.get().catcher().collectCatcherDetail(catcherId, { detail ->
            val map = catchers.value?.associateBy { it.catcher.id }?.toMutableMap()
                ?: mutableMapOf()
            map[catcherId] = detail
            viewModelScope.launch {
                catchers.value = map.values.toList()
            }
        }, { err ->
            println(err)
        })
    }
}

class MockCatcherViewModel : CatcherPageViewModel() {

    override fun start() {
        catchers.value = List(6) {

            CatcherDetail(
                catcher = Catcher(
                    id = it,
                    sender = "",
                    regexId = 1,
                    description = "asd",
                    catchCount = 25,
                    status = 1
                ),
                actions = listOf(
                    ActionDetail(
                        action = CatcherAction(
                            catcherId = it,
                            actionId = 1
                        ), detail = Action(
                            id = 1,
                            icon = "ContentCopy",
                            name = "Copy",
                        )
                    ),

                    ActionDetail(
                        action = CatcherAction(
                            catcherId = it,
                            actionId = 2
                        ), detail = Action(
                            id = 2,
                            icon = "ContentCopy",
                            name = "Mic",
                        )
                    ),

                    ),
                regex = regexList()[0],
                stat = listOf()
            )

        }

        allActions.value = actionList()
    }
}