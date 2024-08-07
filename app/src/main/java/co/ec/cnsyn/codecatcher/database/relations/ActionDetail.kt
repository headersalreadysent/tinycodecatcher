package co.ec.cnsyn.codecatcher.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import co.ec.cnsyn.codecatcher.database.action.Action
import co.ec.cnsyn.codecatcher.database.catcheraction.CatcherAction

data class ActionDetail(

    @Embedded val action: CatcherAction,
    @Relation(
        parentColumn = "actionId",
        entityColumn = "id"
    )
    val detail: Action
)