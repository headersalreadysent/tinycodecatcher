package co.ec.cnsyn.codecatcher.database


import androidx.room.Database
import androidx.room.RoomDatabase
import co.ec.cnsyn.codecatcher.database.regex.Regex

@Database(entities = [Regex::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
}