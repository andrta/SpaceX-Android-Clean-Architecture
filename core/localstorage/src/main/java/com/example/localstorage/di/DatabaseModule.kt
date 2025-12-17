package com.example.localstorage.di

import android.content.Context
import androidx.room.Room
import com.example.localstorage.dao.LaunchDao
import com.example.localstorage.db.MIGRATION_1_2
import com.example.localstorage.db.SpaceXDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SpaceXDatabase {
        return Room.databaseBuilder(
            context,
            SpaceXDatabase::class.java,
            "spacex_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideLaunchDao(database: SpaceXDatabase): LaunchDao {
        return database.launchDao()
    }
}
