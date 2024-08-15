package co.ec.cnsyn.codecatcher.database.catcheraction

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.helpers.async
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(
    indices = [
        Index(value = ["catcherId", "actionId"]),
        Index(value = ["status"], orders = [Index.Order.DESC])
    ]
)
data class CatcherAction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var catcherId: Int,
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

    fun updateParam(updatedParams: Map<String, String>?) {
        params = Json.encodeToString(updatedParams)
        if (id > 0) {
            async({
                return@async DB.get().catcherAction().updateParams(id, params = params)
            })
        }
    }

}