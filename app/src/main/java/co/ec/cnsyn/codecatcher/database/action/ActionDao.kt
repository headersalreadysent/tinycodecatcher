package co.ec.cnsyn.codecatcher.database.action

import androidx.room.Dao
import androidx.room.Query
import co.ec.cnsyn.codecatcher.database.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionDao : BaseDao<Action> {

    @Query("SELECT * FROM `action`")
    fun getAllItems(): List<Action>

}