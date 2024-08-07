package co.ec.cnsyn.codecatcher.pages.catcher


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.action.Action
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.catcher.CatcherDao
import co.ec.cnsyn.codecatcher.database.catcher.CatcherDao.CatcherDetail
import co.ec.cnsyn.codecatcher.database.catcheraction.CatcherAction
import co.ec.cnsyn.codecatcher.database.code.Code
import co.ec.cnsyn.codecatcher.database.code.CodeDao
import co.ec.cnsyn.codecatcher.database.regex.Regex
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.helpers.async
import co.ec.cnsyn.codecatcher.helpers.unix

import kotlin.random.Random

open class CatcherPageViewModel : ViewModel() {

    var catchers = MutableLiveData<List<CatcherDao.CatcherDetail>>(listOf())

    init {
        start()
    }

    open fun start() {
        //load stats
        DB.get().catcher().getCatchersWithDetails({
            catchers.value = it
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
                regex = Regex(id = 1, regex = "[0-9]{6}", "6 karakter", catchCount = 1, 1),
                codes = listOf()
            )

        }
    }
}