package com.example.data.repository

import com.example.data.LaunchRemoteDataSource
import com.example.domain.featureflags.FeatureFlagProvider
import com.example.domain.models.Launch
import com.example.domain.repository.LaunchRepository
import com.example.domain.result.DomainResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Named

class LaunchRepositoryImpl @Inject constructor(
    @get:Named("graphql") private val graphqlSource: LaunchRemoteDataSource,
    @get:Named("rest") private val restSource: LaunchRemoteDataSource,
    private val featureFlags: FeatureFlagProvider,
) : LaunchRepository {
    override fun getLastLaunches(forceRefresh: Boolean): Flow<DomainResult<List<Launch>>> = flow {
        try {
            val launches = if (featureFlags.isGraphQlEnabled) {
                graphqlSource.getLastLaunches()
            } else {
                restSource.getLastLaunches()
            }
            emit(DomainResult.Success(launches))

        } catch (e: Exception) {
            emit(DomainResult.Error(e))
        }
    }
}
