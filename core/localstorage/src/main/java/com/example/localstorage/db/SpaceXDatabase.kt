package com.example.localstorage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.localstorage.converters.RoomTypeConverters
import com.example.localstorage.dao.LaunchDao
import com.example.localstorage.entities.LaunchEntity

@Database(
    entities = [LaunchEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(RoomTypeConverters::class)
abstract class SpaceXDatabase : RoomDatabase() {
    abstract fun launchDao(): LaunchDao
}
