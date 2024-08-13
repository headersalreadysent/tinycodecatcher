package co.ec.cnsyn.codecatcher.database.code

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["date"], orders = [Index.Order.DESC]),
        Index(value = ["catcherId"])
    ]
)
data class Code(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var date: Long,
    val catcherId: Int,
    var sender: String = "",
    var sms: String = "",
    var code: String = "",
)