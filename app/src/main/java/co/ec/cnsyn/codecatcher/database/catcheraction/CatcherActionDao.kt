package co.ec.cnsyn.codecatcher.database.catcheraction

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import co.ec.cnsyn.codecatcher.database.BaseDao
import co.ec.cnsyn.codecatcher.database.catcheraction.CatcherAction
import co.ec.cnsyn.codecatcher.database.code.Code
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.database.relations.CodeWithCatcher
import kotlinx.coroutines.flow.Flow

@Dao
interface CatcherActionDao : BaseDao<CatcherAction> {

    @Query("SELECT * FROM catcheraction")
    fun getAllItems(): List<CatcherAction>


    @Query("SELECT * FROM catcheraction WHERE catcherId IN (:catcherIds)")
    fun getWithDetail(catcherIds: IntArray): List<ActionDetail>


}