package com.example.data.datasource

import com.example.data.mappers.toDomain
import com.example.data.services.SpaceXApiService
import com.example.domain.models.Launch
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class RestLaunchDataSource @Inject constructor(
    private val api: SpaceXApiService
) : LaunchRemoteDataSource {
    override suspend fun getLastLaunches(): List<Launch> = coroutineScope {
        val launchDtoList = api.getLaunches()
        val distinctRocketIdList = launchDtoList.map { it.rocketId }.distinct()
        val rocketsDeferred: List<Deferred<Pair<String, String>>> =
            distinctRocketIdList.map { rocketId ->
                async {
                    rocketId to getRocketNameSafely(rocketId)
                }
            }

        val rocketNameMap: Map<String, String> = rocketsDeferred.awaitAll().toMap()

        return@coroutineScope launchDtoList.map { dto ->
            val resolvedName = rocketNameMap[dto.rocketId] ?: "Unknown Rocket Name"
            dto.toDomain(rocketName = resolvedName)
        }
    }

    /**
     * Helper function to fetch the rocket name while handling potential errors.
     * If the call fails, we don't want to crash the entire launch list.
     */
    private suspend fun getRocketNameSafely(id: String): String {
        return try {
            val rocketDto = api.getRocket(id)
            rocketDto.name
        } catch (e: Exception) {
            e.printStackTrace()
            "Unknown Rocket Name for ID: $id"
        }
    }
}
