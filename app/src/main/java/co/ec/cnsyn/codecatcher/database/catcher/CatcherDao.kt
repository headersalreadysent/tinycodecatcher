package co.ec.cnsyn.codecatcher.database.catcher

import androidx.room.Dao
import androidx.room.Query
import co.ec.cnsyn.codecatcher.database.BaseDao
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithActions
import kotlinx.coroutines.flow.Flow

@Dao
interface CatcherDao : BaseDao<Catcher> {

    @Query("SELECT * FROM catcher")
    fun getAllItems(): List<Catcher>


    @Query("SELECT count(id) FROM catcher WHERE status=1")
    fun getActiveCount(): Int



}