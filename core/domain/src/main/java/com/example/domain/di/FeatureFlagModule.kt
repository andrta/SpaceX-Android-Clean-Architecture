package com.example.domain.di

import com.example.domain.featureflags.FeatureFlagProvider
import com.example.domain.featureflags.FeatureFlagProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FeatureFlagModule {
    @Provides
    fun provideFeatureFlagProvider(): FeatureFlagProvider = FeatureFlagProviderImpl()
}
