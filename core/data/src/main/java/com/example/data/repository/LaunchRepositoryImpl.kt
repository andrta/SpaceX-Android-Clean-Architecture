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
    @param:Named("graphql") private val graphqlSource: LaunchRemoteDataSource,
    @param:Named("rest") private val restSource: LaunchRemoteDataSource,
    private val localSource: LaunchLocalDataSource,
    private val featureFlags: FeatureFlagProvider
) : LaunchRepository {

    override fun getLastLaunches(forceRefresh: Boolean): Flow<DomainResult<List<Launch>>> = flow {
        var recentNetworkError: DataError? = null
        if (forceRefresh) {
            try {
                val remoteLaunches = if (featureFlags.isGraphQlEnabled()) {
                    graphqlSource.getLastLaunches()
                } else {
                    restSource.getLastLaunches()
                }
                localSource.saveLaunches(remoteLaunches)
            } catch (e: Exception) {
                e.printStackTrace()
                recentNetworkError = e.toDataError()
            }
        }

        val localFlow = localSource.getLaunches().map { list ->
            val result: DomainResult<List<Launch>> = if (list.isNotEmpty()) {
                DomainResult.Success(list)
            } else {
                if (recentNetworkError != null) {
                    DomainResult.Failure(recentNetworkError)
                } else {
                    DomainResult.Success(emptyList())
                }
            }
            result
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
    private fun FeatureFlagProvider.isGraphQlEnabled(): Boolean = isGraphQlEnabled
}
