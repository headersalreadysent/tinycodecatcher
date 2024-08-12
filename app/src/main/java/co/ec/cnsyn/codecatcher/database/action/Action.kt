package co.ec.cnsyn.codecatcher.database.action

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Action(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val key: String = "",
    var name: String = "",
    var description: String = "",
    var icon: String = "",
    var action: String = "",
    var defaultParams: String = "{}"
)