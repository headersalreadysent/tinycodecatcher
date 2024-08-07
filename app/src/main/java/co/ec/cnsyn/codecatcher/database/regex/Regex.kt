package co.ec.cnsyn.codecatcher.database.regex

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Regex(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val regex: String,
    var description: String,
    var catchCount: Int = 0,
    var status: Int = 1
)