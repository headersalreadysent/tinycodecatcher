package co.ec.cnsyn.codecatcher.database.catcheraction

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import co.ec.cnsyn.codecatcher.database.BaseDao
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail

@Dao
interface CatcherActionDao : BaseDao<CatcherAction> {

    @Query("SELECT * FROM catcheraction WHERE catcherId=:catcherId AND actionId=:actionId")
    fun getOne(catcherId: Int, actionId: Int): CatcherAction?

    @Query("SELECT * FROM catcheraction")
    fun getAllItems(): List<CatcherAction>

    @Transaction
    @Query("SELECT * FROM catcheraction WHERE catcherId IN (:catcherIds) AND status=1")
    fun getWithDetail(catcherIds: IntArray): List<ActionDetail>

    @Query("UPDATE catcheraction SET status = 0 WHERE catcherId =:catcherIds AND actionId=:actionId")
    fun disable(catcherIds: Int, actionId: Int)

    @Query("UPDATE catcheraction SET params=:params WHERE id=:id")
    fun updateParams(id: Int, params: String)

    @Query("DELETE FROM  catcheraction WHERE catcherId=:id")
    fun deleteCatcher(id: Int)


}