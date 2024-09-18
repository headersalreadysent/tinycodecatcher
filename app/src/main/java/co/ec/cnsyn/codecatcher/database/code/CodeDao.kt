package co.ec.cnsyn.codecatcher.database.code

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import co.ec.cnsyn.codecatcher.database.BaseDao
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.database.relations.CodeWithCatcher

@Dao
interface CodeDao : BaseDao<Code> {

    @Query("SELECT * FROM code")
    fun getAllItems(): List<Code>


    @Query("SELECT sms FROM code")
    fun getAllText(): List<String>

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

    data class CodeDate(var date: Int, var catcherId: Int)

    @Query("SELECT date,catcherId FROM code WHERE date > :start")
    fun getCalendar(start: Long): List<CodeDate>

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

    data class CatcherCountStat(val sender: String, val name: String, val count: Int)

    @Query(
        """
SELECT t.sender,r.name,count(c.id) AS count 
FROM Code c 
LEFT JOIN Catcher t ON c.catcherId=t.id 
LEFT JOIN regex r ON r.id=t.regexId
WHERE t.status=1
GROUP BY c.catcherId
    """
    )
    fun catcherCountStats(): List<CatcherCountStat>

    @Query(
        """
SELECT c.sender,c.sender as name,count(c.id) AS count 
FROM Code c 
GROUP BY c.sender ORDER BY count DESC LIMIT 5
    """
    )
    fun senderCountStat(): List<CatcherCountStat>


    class Latest(var code: Code, var catcher: Catcher?, var actions: List<ActionDetail>)

    /**
     * get latest
     */
    fun getLatest(limit: Int = 10): List<Latest> {
        val db = DB.get()
        val codes = db.code().getLatestCodes(limit)
        val catcherIds = codes.map { it.catcherId }.toSet().toList()

        val catchers = db.catcher().getCatchers(catcherIds.toIntArray()).associateBy { it.id }
        val action = db.catcherAction().getWithDetail(catcherIds.toIntArray())
        return codes.map { code ->
            return@map Latest(
                code = code,
                catcher = catchers[code.catcherId],
                actions = action.filter { it.action.catcherId == code.catcherId }
                    .sortedBy { it.action.actionId }
            )
        }
    }

}