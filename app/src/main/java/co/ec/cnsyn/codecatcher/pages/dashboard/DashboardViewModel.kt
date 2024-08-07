package co.ec.cnsyn.codecatcher.pages.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.relations.CodeWithCatcher
import co.ec.cnsyn.codecatcher.helpers.async

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DashboardViewModel : ViewModel() {


    var stats = MutableLiveData(
        mapOf(
            "catcher" to 0,
            "code" to 0
        )
    )

    var codes : Flow<List<CodeWithCatcher>> = flowOf()

    init {
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
        codes=DB.get().code().getLatestCodes()

    }
}