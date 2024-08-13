package co.ec.cnsyn.codecatcher.database.catcher

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["regexId"]),
        Index(value = ["status"])
    ]
)
data class Catcher(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var regexId: Int = 0,
    var sender: String = "",
    var description: String = "",
    var catchCount: Int = 0,
    var status: Int = 1
)