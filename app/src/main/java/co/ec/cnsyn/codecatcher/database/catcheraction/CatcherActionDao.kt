package co.ec.cnsyn.codecatcher.database.catcheraction

import androidx.room.Dao
import androidx.room.Query
import co.ec.cnsyn.codecatcher.database.BaseDao
import co.ec.cnsyn.codecatcher.database.catcheraction.CatcherAction
import co.ec.cnsyn.codecatcher.database.code.Code
import kotlinx.coroutines.flow.Flow

@Dao
interface CatcherActionDao : BaseDao<CatcherAction> {

    @Query("SELECT * FROM catcheraction")
    fun getAllItems(): List<CatcherAction>

}