package co.ec.cnsyn.codecatcher.database.catcheraction

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.json.Json

@Entity
data class CatcherAction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val catcherId: Int,
    var actionId: Int,
    var params: String = "",
    var status: Int = 1
) {

    fun params(): Map<String, String> {
        val paramText = if (params == "") "{}" else params
        val dynamicType: Map<String, String> = Json.decodeFromString(paramText)
        // Convert dynamic type to Map<String, String>
        return dynamicType.mapValues { it.value }
    }


}