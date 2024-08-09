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


    @Query("SELECT * FROM code ORDER BY date DESC LIMIT :limit")
    fun getLatestCodes(limit: Int = 20): List<Code>

    @Query("SELECT * FROM code WHERE catcherId=:catcherId AND date >=:date AND date <= :date+86400")
    fun getCatcherDay(catcherId: Int, date: Int): List<Code>


    @Query("SELECT count(id) FROM code")
    fun getCount(): Int


    class Stat(var count: Int, var catcherId: Int, var start: Int)

    @Query(
        """
        SELECT count(id) AS count, catcherId, date-date%86400 as start
        FROM code
        WHERE date >= strftime('%s', 'now', 'start of day', 'localtime')-86400*:dayCount
        GROUP BY catcherId || '-' || strftime('%Y-%m-%d', datetime(date, 'unixepoch')) 
        ORDER BY catcherId ASC, date DESC
    """
    )
    fun getStats(dayCount: Int = 28): List<Stat>

    @Query(
        """
        WITH RECURSIVE date_range AS (
    SELECT strftime('%s', 'now', 'start of day', 'localtime') AS day, 0 AS row_num
    UNION ALL
    SELECT day - 86400, row_num + 1
    FROM date_range
    WHERE row_num < :dayCount - 1
),
item_counts AS (SELECT 
    dr.day,
    strftime('%Y-%m-%d', datetime(dr.day, 'unixepoch')) ,
    COUNT(c.id) AS item_count
FROM 
    date_range dr
LEFT JOIN 
    code c 
    ON c.date > dr.day AND c.date < dr.day+86400 AND c.catcherId=:catcherId
GROUP BY 
    dr.day
ORDER BY 
    dr.day DESC)
    Select avg(item_count) AS avg from item_counts

    """
    )
    fun getAverage(catcherId: Int, dayCount: Int = 7): Float

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