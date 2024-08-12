package co.ec.cnsyn.codecatcher.database.relations

import kotlinx.serialization.json.Json


import androidx.room.ColumnInfo
import androidx.room.DatabaseView

@DatabaseView(
    """
    SELECT ref.catcherId,ref.actionId, c.sender, c.description, c.catchCount, a.name, a.icon, a.`action`,ref.params, ref.status
    FROM catcher c
    JOIN catcheraction ref ON c.id = ref.catcherId
    JOIN `action` a ON ref.actionId = a.id
"""
)
data class CatcherWithActions(
    @ColumnInfo(name = "catcherId") val catcherId: Int,
    @ColumnInfo(name = "actionId") val actionId: Int,
    @ColumnInfo(name = "sender") val sender: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "catchCount") val catchCount: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "icon") val icon: String,
    @ColumnInfo(name = "action") val action: String,
    @ColumnInfo(name = "params") val params: String,
    @ColumnInfo(name = "status") val status: Int,
) {

    fun params(): Map<String, String> {
        val paramText = if (params == "") "{}" else params
        val dynamicType: Map<String, String> = Json.decodeFromString(paramText)
        // Convert dynamic type to Map<String, String>
        return dynamicType.mapValues { it.value }
    }
}