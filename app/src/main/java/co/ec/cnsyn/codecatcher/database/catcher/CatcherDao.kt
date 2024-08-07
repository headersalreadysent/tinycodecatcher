package co.ec.cnsyn.codecatcher.database.catcher

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import co.ec.cnsyn.codecatcher.database.BaseDao
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithActions
import kotlinx.coroutines.flow.Flow

@Dao
interface CatcherDao : BaseDao<Catcher> {

    @Query("SELECT * FROM catcher")
    fun getAllItems(): List<Catcher>

    @Query("SELECT * FROM catcher WHERE id IN (:ids)")
    fun getCatchers(ids: IntArray): List<Catcher>

    @Query("SELECT count(id) FROM catcher WHERE status=1")
    fun getActiveCount(): Int




}