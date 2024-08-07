package co.ec.cnsyn.codecatcher.database.catcher

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import co.ec.cnsyn.codecatcher.database.BaseDao
import co.ec.cnsyn.codecatcher.database.DB
import co.ec.cnsyn.codecatcher.database.code.Code
import co.ec.cnsyn.codecatcher.database.code.CodeDao.Latest
import co.ec.cnsyn.codecatcher.database.regex.Regex
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithActions
import co.ec.cnsyn.codecatcher.helpers.async
import co.ec.cnsyn.codecatcher.pages.catcher.CatcherPage
import kotlinx.coroutines.flow.Flow

@Dao
interface CatcherDao : BaseDao<Catcher> {

    @Query("SELECT * FROM catcher")
    fun getAllItems(): List<Catcher>

    @Query("SELECT * FROM catcher WHERE id IN (:ids)")
    fun getCatchers(ids: IntArray): List<Catcher>

    @Query("SELECT count(id) FROM catcher WHERE status=1")
    fun getActiveCount(): Int


    data class CatcherDetail(
        var catcher: Catcher,
        var actions: List<ActionDetail>,
        val regex: Regex,
        val codes: List<Code>
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

            val codes = db.code().getLastItemsPerCatcher()

            return@async catchers.map { catcher ->
                return@map CatcherDetail(
                    catcher = catcher,
                    actions = action.filter { it.action.catcherId == catcher.id }
                        .sortedBy { it.action.actionId },
                    regex = regexes[catcher.regexId]!!,
                    codes = codes.filter { it.catcherId == catcher.id }
                )
            }
        }, then, err)
    }

}