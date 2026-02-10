package com.example.data.di

import com.example.data.datasource.GraphQlLaunchDataSource
import com.example.data.datasource.LaunchRemoteDataSource
import com.example.data.datasource.RestLaunchDataSource
import com.example.data.repository.LaunchRepositoryImpl
import com.example.domain.repository.LaunchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    @Named("graphql")
    fun bindGraphQlDataSource(
        impl: GraphQlLaunchDataSource
    ): LaunchRemoteDataSource

    @Binds
    @Named("rest")
    fun bindRestDataSource(
        impl: RestLaunchDataSource
    ): LaunchRemoteDataSource

    @Binds
    fun bindLaunchRepository(
        impl: LaunchRepositoryImpl
    ): LaunchRepository
}
