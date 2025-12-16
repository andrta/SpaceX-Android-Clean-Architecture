package com.example.data

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

        // 3. Download rocket details in PARALLEL
        // Create a list of "promises" (Deferred)
        val rocketsDeferred: List<Deferred<Pair<String, String>>> =
            distinctRocketIdList.map { rocketId ->
                async {
                    // Return a pair ID -> Name
                    rocketId to getRocketNameSafely(rocketId)
                }
            }

        // Wait for all parallel calls to finish and create a Map
        // Result: Map<"5e9d...", "Falcon 9">
        val rocketNameMap: Map<String, String> = rocketsDeferred.awaitAll().toMap()

        // 4. Final mapping DTO -> Domain
        // Now we have everything needed to create the clean Launch object
        return@coroutineScope launchDtoList.map { dto ->
            // Retrieve the name from the map, or use a fallback
            val resolvedName = rocketNameMap[dto.rocketId] ?: "Unknown Rocket Name"

            // Use the mapper we created earlier
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
            rocketDto.name // Assuming RocketDto has a 'name' field
        } catch (e: Exception) {
            e.printStackTrace()
            "Unknown Rocket Name for ID: $id" // Fallback in case of an API error for this rocket
        }
    }
}
