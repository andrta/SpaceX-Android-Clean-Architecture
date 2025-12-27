package com.example.data.repository

import com.example.data.datasource.LaunchLocalDataSource
import com.example.data.datasource.LaunchRemoteDataSource
import com.example.data.mappers.toDataError
import com.example.domain.featureflags.FeatureFlagProvider
import com.example.domain.models.Launch
import com.example.domain.repository.LaunchRepository
import com.example.domain.result.DataError
import com.example.domain.result.DomainResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class LaunchRepositoryImpl @Inject constructor(
    @Named("graphql") private val graphqlSource: LaunchRemoteDataSource,
    @Named("rest") private val restSource: LaunchRemoteDataSource,
    private val localSource: LaunchLocalDataSource,
    private val featureFlags: FeatureFlagProvider
) : LaunchRepository {

    override fun getLastLaunches(forceRefresh: Boolean): Flow<DomainResult<List<Launch>>> = flow {
        var recentNetworkError: DataError? = null

        // 1. NETWORK UPDATE (Se richiesto)
        try {
            val remoteLaunches = if (featureFlags.isGraphQlEnabled) {
                graphqlSource.getLastLaunches()
            } else {
                restSource.getLastLaunches()
            }
            localSource.saveLaunches(remoteLaunches)
        } catch (e: Exception) {
            recentNetworkError = e.toDataError()
            e.printStackTrace()
        }

        // 2. EMIT FROM DB
        val localFlow = localSource.getLaunches().map { list ->
            if (list.isNotEmpty()) {
                DomainResult.Success(list)
            } else {
                if (recentNetworkError != null) {
                    DomainResult.Failure(recentNetworkError)
                } else {
                    DomainResult.Success(emptyList())
                }
            }
        }
        emitAll(localFlow)
    }

    override fun getLaunchDetails(launchId: String): Flow<DomainResult<Launch>> = flow {
        val launch = localSource.getLaunchById(launchId)
        if (launch != null) {
            emit(DomainResult.Success(launch))
        } else {
            emit(DomainResult.Failure(DataError.Local.DiskRead))
        }
    }
}
