package co.ec.cnsyn.codecatcher.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: T) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg entities: T)

    @Update
    fun update(entity: T)

    @Delete
    fun delete(entity: T)
}