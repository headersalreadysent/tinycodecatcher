package co.ec.cnsyn.codecatcher.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg entities: T)

    @Update
    fun update(entity: T)

    @Delete
    fun delete(entity: T)

    @RawQuery
    fun query(query: SupportSQLiteQuery): List<T>

    fun select(sql: String): List<T> {
        return query(SimpleSQLiteQuery(sql))
    }
}