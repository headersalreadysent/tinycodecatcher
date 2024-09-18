package co.ec.cnsyn.codecatcher.database.catcher

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import co.ec.cnsyn.codecatcher.database.BaseDao
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.regex.Regex
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithRegex
import co.ec.cnsyn.codecatcher.helpers.async
import co.ec.cnsyn.codecatcher.helpers.unix

@Dao
interface CatcherDao : BaseDao<Catcher> {

    @Query("SELECT * FROM catcher WHERE id=:id")
    fun getOne(id: Int): Catcher

    @Query("SELECT * FROM catcher")
    fun getAllItems(): List<Catcher>

    @Query("SELECT * FROM catcher WHERE id IN (:ids)")
    fun getCatchers(ids: IntArray): List<Catcher>

    @Query("SELECT count(id) FROM catcher WHERE status=1")
    fun getActiveCount(): Int

    @Transaction
    @Query("SELECT * FROM catcher WHERE status=1")
    fun getActiveCatchersWithRegexes(): List<CatcherWithRegex>

    @Query("""
        UPDATE catcher 
        SET catchCount = (SELECT count(id) AS count 
        FROM code WHERE code.catcherId = catcher.id)
    """)
    fun fixCatchersCounts()

    data class CatcherDetail(
        var catcher: Catcher,
        var actions: List<ActionDetail>,
        val regex: Regex,
        val stat: List<Int>,
        val avg: Map<Int, Float>
    )

    /**
     * get latest
     */
    fun getCatchersWithDetails(
        then: (res: List<CatcherDetail>) -> Unit = { _ -> },
        err: (res: Throwable) -> Unit = { _ -> }
    ) {
        async({
            val db = DB.get()
            //get all catchers
            val catchers = db.catcher().getAllItems()
            //collect actions
            val action = db.catcherAction().getWithDetail(catchers.map { it.id }.toIntArray())
            val regexes = db.regex().getRegexes(catchers.map { it.regexId }.toIntArray())
                .associateBy { it.id }
            val stats = db.code().getCalendar(unix() - 28 * 86400)
            return@async catchers.map { catcher ->
                return@map CatcherDetail(
                    catcher = catcher,
                    actions = action.filter { it.action.catcherId == catcher.id }
                        .sortedBy { it.action.actionId },
                    regex = regexes[catcher.regexId]!!,
                    stat = stats.filter { it.catcherId == catcher.id }.map { it.date },
                    avg = mapOf(
                        7 to db.code().getAverage(catcher.id, 7),
                        14 to db.code().getAverage(catcher.id, 14),
                        30 to db.code().getAverage(catcher.id, 30)
                    )
                )
            }
        }, then, err)
    }

    fun collectCatcherDetail(
        id: Int,
        then: (res: CatcherDetail) -> Unit = { _ -> },
        err: (res: Throwable) -> Unit = { _ -> }
    ) {
        async({
            val db = DB.get()
            //get all catchers
            val catcher = db.catcher().getOne(id)
            //collect actions
            val action = db.catcherAction().getWithDetail(intArrayOf(id))
            val regexes = db.regex().getRegexes(intArrayOf(catcher.regexId))
                .associateBy { it.id }
            return@async CatcherDetail(
                catcher = catcher,
                actions = action.filter { it.action.catcherId == catcher.id }
                    .sortedBy { it.action.actionId },
                regex = regexes[catcher.regexId]!!,
                stat = db.code().getCalendar(unix() - 28 * 86400).filter { it.catcherId == id }
                    .map { it.date },
                avg = mapOf(
                    7 to db.code().getAverage(catcher.id, 7),
                    14 to db.code().getAverage(catcher.id, 14),
                    30 to db.code().getAverage(catcher.id, 30)
                )
            )
        }, then, err)
    }

}