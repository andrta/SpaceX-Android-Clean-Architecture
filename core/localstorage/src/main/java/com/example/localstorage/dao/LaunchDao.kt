package com.example.localstorage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.localstorage.entities.LaunchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LaunchDao {
    @Query("SELECT * FROM launches ORDER BY launchDate DESC")
    fun getAllLaunches(): Flow<List<LaunchEntity>>

    @Query("SELECT * FROM launches WHERE id = :id")
    suspend fun getLaunchById(id: String): LaunchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(launches: List<LaunchEntity>)

    @Query("DELETE FROM launches")
    suspend fun clearAll()

    @Transaction
    suspend fun clearAndInsert(launches: List<LaunchEntity>) {
        clearAll()
        insertAll(launches)
    }
}
