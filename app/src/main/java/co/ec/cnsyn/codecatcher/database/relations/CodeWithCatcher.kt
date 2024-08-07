package co.ec.cnsyn.codecatcher.database.relations



import androidx.room.Embedded
import androidx.room.Relation
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.code.Code

data class CodeWithCatcher(

    @Embedded val code: Code,
    @Relation(
        parentColumn = "catcherId",
        entityColumn = "id"
    )
    val catcher: Catcher
)