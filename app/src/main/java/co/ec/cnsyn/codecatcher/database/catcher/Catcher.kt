package co.ec.cnsyn.codecatcher.database.catcher

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Catcher(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val regexId: Int,
    var sender: String,
    var description: String = "",
    var catchCount: Int = 0,
    var status: Int = 1
)