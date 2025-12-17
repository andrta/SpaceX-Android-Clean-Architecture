package com.example.data.di

import com.example.data.datasource.GraphQlLaunchDataSource
import com.example.data.datasource.LaunchRemoteDataSource
import com.example.data.datasource.RestLaunchDataSource
import com.example.data.repository.LaunchRepositoryImpl
import com.example.domain.repository.LaunchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    @Named("graphql")
    fun provideGraphQlDataSource(
        dataSource: GraphQlLaunchDataSource
    ): LaunchRemoteDataSource =
        dataSource

    @Provides
    @Named("rest")
    fun provideRestDataSource(
        dataSource: RestLaunchDataSource
    ): LaunchRemoteDataSource = dataSource

    @Provides
    fun provideLaunchRepository(
        repository: LaunchRepositoryImpl
    ): LaunchRepository = repository
}
