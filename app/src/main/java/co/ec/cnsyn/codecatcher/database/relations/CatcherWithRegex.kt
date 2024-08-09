package co.ec.cnsyn.codecatcher.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.regex.Regex

data class CatcherWithRegex(

    @Embedded val catcher: Catcher,
    @Relation(
        parentColumn = "regexId",
        entityColumn = "id"
    )
    val regex: Regex,
    @Relation(
        parentColumn = "id",
        entityColumn = "catcherId"
    )
    val actions: List<CatcherWithActions>
)