package co.ec.cnsyn.codecatcher.database.code

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import co.ec.cnsyn.codecatcher.database.BaseDao
import co.ec.cnsyn.codecatcher.database.relations.CodeWithCatcher
import kotlinx.coroutines.flow.Flow

@Dao
interface CodeDao : BaseDao<Code> {

    @Query("SELECT * FROM code")
    fun getAllItems(): List<Code>


    @Transaction
    @Query(
        """
        SELECT * FROM code
        INNER JOIN catcher ON code.catcherId = catcher.id
        WHERE code.catcherId = :catcherId
    """
    )
    fun getCodeWithCatcher(catcherId: Int): List<CodeWithCatcher>


    @Transaction
    @Query(
        """
        SELECT * FROM code
        INNER JOIN catcher ON code.catcherId = catcher.id
        WHERE catcher.status =1 
        ORDER BY code.id DESC LIMIT :limit
    """
    )
    fun getLatestCodes(limit: Int = 20): Flow<List<CodeWithCatcher>>


    @Query("SELECT count(id) FROM code")
    fun getCount(): Int

}