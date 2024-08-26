package co.ec.cnsyn.codecatcher.database


import androidx.room.Database
import androidx.room.RoomDatabase
import co.ec.cnsyn.codecatcher.database.action.Action
import co.ec.cnsyn.codecatcher.database.action.ActionDao
import co.ec.cnsyn.codecatcher.database.catcher.Catcher
import co.ec.cnsyn.codecatcher.database.catcher.CatcherDao
import co.ec.cnsyn.codecatcher.database.catcheraction.CatcherAction
import co.ec.cnsyn.codecatcher.database.catcheraction.CatcherActionDao
import co.ec.cnsyn.codecatcher.database.code.Code
import co.ec.cnsyn.codecatcher.database.code.CodeDao
import co.ec.cnsyn.codecatcher.database.regex.Regex
import co.ec.cnsyn.codecatcher.database.regex.RegexDao
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithActions

@Database(
    entities = [Action::class, Catcher::class, CatcherAction::class, Code::class, Regex::class],
    views = [CatcherWithActions::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun code(): CodeDao
    abstract fun catcher(): CatcherDao
    abstract fun action(): ActionDao
    abstract fun catcherAction(): CatcherActionDao
    abstract fun regex(): RegexDao
}