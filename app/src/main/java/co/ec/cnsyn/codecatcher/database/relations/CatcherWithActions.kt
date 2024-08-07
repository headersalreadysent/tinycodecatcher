package co.ec.cnsyn.codecatcher.database.relations

import androidx.room.DatabaseView
import co.ec.cnsyn.codecatcher.database.catcher.Catcher


import androidx.room.Embedded
import androidx.room.Relation
import co.ec.cnsyn.codecatcher.database.action.Action


@DatabaseView
data class CatcherWithActions(

    @Embedded val catcher: Catcher,
    @Relation(
        parentColumn = "id",
        entityColumn = "catcherId"
    )
    val actions: List<Action>
)
