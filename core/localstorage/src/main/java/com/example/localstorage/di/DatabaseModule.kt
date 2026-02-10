package com.example.localstorage.di

import android.content.Context
import androidx.room.Room
import com.example.data.datasource.LaunchLocalDataSource
import com.example.localstorage.dao.LaunchDao
import com.example.localstorage.datasource.LaunchLocalDataSourceImpl
import com.example.localstorage.db.MIGRATION_2_3
import com.example.localstorage.db.SpaceXDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {
    @Binds
    abstract fun provideLocalDataSource(
        launchLocalDataSourceImpl: LaunchLocalDataSourceImpl
    ): LaunchLocalDataSource

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): SpaceXDatabase {
            return Room.databaseBuilder(
                context,
                SpaceXDatabase::class.java,
                "spacex_database"
            )
                .addMigrations(MIGRATION_2_3)
                .build()
        }

        @Provides
        fun provideLaunchDao(database: SpaceXDatabase): LaunchDao {
            return database.launchDao()
        }
    }
}
