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
    @get:Named("graphql") private val graphqlSource: LaunchRemoteDataSource,
    @get:Named("rest") private val restSource: LaunchRemoteDataSource,
    private val localSource: LaunchLocalDataSource,
    private val featureFlags: FeatureFlagProvider
) : LaunchRepository {

    override fun getLastLaunches(forceRefresh: Boolean): Flow<DomainResult<List<Launch>>> = flow {

        // Variable to capture any network failure occurring during the update process.
        // We capture it here to decide later whether to expose it to the UI.
        var recentNetworkError: DataError? = null

        // 1. NETWORK UPDATE ATTEMPT (Network -> DB)
        // We try to fetch fresh data and save it to the local database.
        try {
            val remoteLaunches = if (featureFlags.isGraphQlEnabled) {
                graphqlSource.getLastLaunches()
            } else {
                restSource.getLastLaunches()
            }
            localSource.saveLaunches(remoteLaunches)

        } catch (e: Exception) {
            // Map the technical exception to a Domain Error.
            // WE DO NOT EMIT IT YET. We store it to check against the cache state later.
            recentNetworkError = e.toDataError()
            e.printStackTrace() // Log for debugging (or send to Crashlytics)
        }

        // 2. SINGLE SOURCE OF TRUTH (DB -> UI)
        // We subscribe to the local database flow.
        val localFlow = localSource.getLaunches().map { list ->
            if (list.isNotEmpty()) {
                // CASE A: We have cached data.
                // We always show the cache, even if the network update failed (Offline-first approach).
                // The user sees the last known good data.
                DomainResult.Success(list)
            } else {
                // CASE B: The cache is empty.
                if (recentNetworkError != null) {
                    // Cache is empty AND Network failed.
                    // The user has no data to see and the update failed. We must propagate the error.
                    DomainResult.Error(recentNetworkError)
                } else {
                    // Cache is empty AND Network succeeded (or wasn't attempted/didn't fail).
                    // This implies a genuine Empty State (the API returned 0 items).
                    DomainResult.Success(emptyList())
                }
            }
        }

        // Bridge the local flow to the repository output
        emitAll(localFlow)
    }
}
