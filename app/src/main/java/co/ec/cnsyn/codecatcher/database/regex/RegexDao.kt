package co.ec.cnsyn.codecatcher.database.regex



import androidx.room.Dao
import androidx.room.Query
import co.ec.cnsyn.codecatcher.database.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface RegexDao : BaseDao<Regex> {

    @Query("SELECT * FROM regex")
    fun getAllItems(): List<Regex>


    @Query("SELECT * FROM regex WHERE id IN (:id)")
    fun getRegexes(id:IntArray): List<Regex>

}