package co.ec.cnsyn.codecatcher.pages.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.action.Action
import co.ec.cnsyn.codecatcher.database.regex.Regex
import co.ec.cnsyn.codecatcher.helpers.async
import co.ec.cnsyn.codecatcher.values.actionList
import co.ec.cnsyn.codecatcher.values.regexList

open class AddViewModel : ViewModel() {

    var regexes = MutableLiveData<List<Regex>>()
    var actions = MutableLiveData<List<Action>>()
    var olderMessages = MutableLiveData<List<String>>()


    init {
        start()
    }

    open fun start() {
        async({ DB.get().regex().getAllItems() }, {
            regexes.value = it
        })

        async({ DB.get().code().getAllText() }, {
            olderMessages.value = it
        })

        async({ DB.get().action().getAllItems()},{
            actions.value=it
        })
    }

}


class MockAddViewModel : AddViewModel() {

    override fun start() {
        regexes.value = regexList()
        actions.value = actionList()
    }
}
