package co.ec.cnsyn.codecatcher.pages.catcher


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.action.Action
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.catcher.CatcherDao.CatcherDetail
import co.ec.cnsyn.codecatcher.database.catcheraction.CatcherAction
import co.ec.cnsyn.codecatcher.database.code.Code
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.helpers.AppLogger
import co.ec.cnsyn.codecatcher.helpers.async
import co.ec.cnsyn.codecatcher.helpers.unix
import co.ec.cnsyn.codecatcher.values.actionList
import co.ec.cnsyn.codecatcher.values.regexList
import kotlinx.coroutines.launch
import kotlin.random.Random

open class CatcherPageViewModel : ViewModel() {

    var catchers = MutableLiveData<List<CatcherDetail>>(listOf())

    var allActions = MutableLiveData<List<Action>>(listOf())

    var dayCodes = MutableLiveData<List<Code>>(listOf())

    init {
        start()
    }

    open fun start() {
        //load stats
        loadcatchers()
        async({
            return@async DB.get().action().getAllItems()
        }, {
            allActions.value = it
        })
    }

    private fun loadcatchers() {
        DB.get().catcher().getCatchersWithDetails({
            catchers.value = it
        }, { err ->
            AppLogger.e("catchers load error",err)
        })
    }

    /**
     * change status action for given catcher
     */
    fun actionStatus(catcherId: Int, action: Action, targetStatus: Boolean = true) {
        async({
            if (!targetStatus) {
                return@async DB.get().catcherAction().disable(catcherId, action.id)
            } else {
                var catcherAction =
                    DB.get().catcherAction().getOne(catcherId, action.id)
                if (catcherAction == null) {
                    catcherAction = CatcherAction(
                        catcherId = catcherId,
                        actionId = action.id,
                        params = "",
                        status = 1
                    )
                }
                catcherAction.status = 1
                return@async DB.get().catcherAction().insert(catcherAction)
            }
        }, {
            reloadOneCatcher(catcherId)
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
            then()
        }, { err ->
            AppLogger.e("reload catcher error",err)
        })
    }

    /**
     * load one day stats
     */
    fun loadDayStats(catcherId: Int, start: Int) {
        async({
            return@async DB.get().code().getCatcherDay(catcherId, start)
        }, {
            dayCodes.value = it
        })
    }

    fun clearDayStat() {
        dayCodes.value = listOf()
    }

    /**
     * delete catcher from page
     */
    fun deleteCatcher(catcherDetail: CatcherDetail, then: () -> Unit = {  }) {
        async({
            DB.get().catcherAction().deleteCatcher(catcherDetail.catcher.id)
            return@async DB.get().catcher().delete(catcherDetail.catcher)
        }, {
            loadcatchers()
            then()
        })
    }
}

class MockCatcherViewModel : CatcherPageViewModel() {

    override fun start() {
        catchers.value = List(6) {

            var now = unix().toInt()
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
                stat = List(60)  {
                    now -= (Random.nextFloat() * 86400).toInt()
                    return@List now
                },
                avg = mapOf(
                    7 to Random.nextFloat(),
                    14 to Random.nextFloat(),
                    30 to Random.nextFloat()
                )
            )

        }

        allActions.value = actionList()
    }
}