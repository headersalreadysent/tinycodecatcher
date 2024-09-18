package co.ec.cnsyn.codecatcher.database.action

import androidx.room.Dao
import androidx.room.Query
import co.ec.cnsyn.codecatcher.database.BaseDao

@Dao
interface ActionDao : BaseDao<Action> {

    @Query("SELECT * FROM `action`")
    fun getAllItems(): List<Action>

    data class ActionCountStat(val name:String,val count:Int)
    @Query("""
SELECT a.name, count(ca.id) as count 
FROM catcheraction ca LEFT JOIN `action` a ON ca.actionId=a.id 
WHERE ca.status=1 
GROUP BY a.id
    """)
    fun actionCountStats(): List<ActionCountStat>


}