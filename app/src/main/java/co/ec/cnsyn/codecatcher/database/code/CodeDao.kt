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


    class Stat(var count: Int, var catcherId: Int, var day: String, var start: Int)

    @Query(
        """
        SELECT count(id) AS count, catcherId ,strftime('%Y-%m-%d', datetime(date, 'unixepoch')) AS day,
         date-date%86400 as start
        FROM code 
        GROUP BY catcherId || "-" || strftime('%Y-%m-%d', datetime(date, 'unixepoch')) 
        ORDER BY catcherId ASC, date DESC
    """
    )
    fun getStats(): List<Stat>

    class Latest(var code: Code, var catcher: Catcher?, var actions: List<ActionDetail>)

    @Query(
        """
      SELECT *
            FROM code
            WHERE id IN (
                SELECT id
                FROM code AS inner_items
                WHERE inner_items.catcherId = code.catcherId
                ORDER BY date DESC
                LIMIT 5
            )
            ORDER BY catcherId, date DESC;
    """
    )
    fun getLastItemsPerCatcher(): List<Code>

    @Query("SELECT *  FROM code WHERE catcherId = :catcherId ORDER BY date DESC LIMIT 5")
    fun getLastItemsOfCatcher(catcherId: Int): List<Code>

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