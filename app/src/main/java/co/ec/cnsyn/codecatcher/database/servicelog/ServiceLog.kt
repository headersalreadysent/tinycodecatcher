package co.ec.cnsyn.codecatcher.database.servicelog

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ServiceLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val receiverId: String,
    val date: String,
    var heartbeatCount: Int = 0,
)