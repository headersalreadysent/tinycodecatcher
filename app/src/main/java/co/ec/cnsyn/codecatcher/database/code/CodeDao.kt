package co.ec.cnsyn.codecatcher.database.code

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import co.ec.cnsyn.codecatcher.database.BaseDao
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.database.relations.CodeWithCatcher
import co.ec.cnsyn.codecatcher.helpers.async
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

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
    @Query("SELECT * FROM code ORDER BY date DESC LIMIT :limit")
    fun getLatestCodes(limit: Int = 20): List<Code>


    @Query("SELECT count(id) FROM code")
    fun getCount(): Int

    class Latest(var code: Code, var catcher: Catcher?, var actions: List<ActionDetail>)

    /**
     * get latest
     */
    fun getLatest(
        then: (res: List<Latest>) -> Unit = { _ -> },
        err: (res: Throwable) -> Unit = { _ -> }
    ) {
        async({
            val db = DB.get()
            val codes = db.code().getLatestCodes(20)
            val catcherIds = codes.map { it.catcherId }.toSet().toList()

            val catchers = db.catcher().getCatchers(catcherIds.toIntArray()).associateBy { it.id }
            val action = db.catcherAction().getWithDetail(catcherIds.toIntArray())
            return@async codes.map { code ->
                return@map Latest(
                    code = code,
                    catcher = catchers.get(code.catcherId),
                    actions = action.filter { it.action.catcherId == code.catcherId }
                        .sortedBy { it.action.actionId }
                )
            }
        }, then, err)
    }

}