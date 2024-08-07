package co.ec.cnsyn.codecatcher.database.relations

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
@DatabaseView(
    """
    SELECT *
    FROM catcher c
    JOIN catcheraction ref ON c.id = ref.catcherId
    JOIN `action` a ON ref.actionId = a.id WHERE ref.status=1
"""
)
data class CatcherWithActions(
    @ColumnInfo(name = "catcherId") val catcherId: Int,
    @ColumnInfo(name = "sender") val sender: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "catchCount") val catchCount: String,
    @ColumnInfo(name = "actionId") val actionId: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "icon") val icon: String,
    @ColumnInfo(name = "action") val action: String,
    @ColumnInfo(name = "params") val params: String,
)