package co.ec.cnsyn.codecatcher.database.catcheraction

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CatcherAction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val catcherId: Int,
    var actionId: Int,
    var paramns: String = "",
    var status: Int = 1
)