package com.example.launches.usecases

import com.example.domain.models.Launch
import com.example.domain.repository.LaunchRepository
import com.example.domain.result.DomainResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLaunchesUseCase @Inject constructor(
    private val repository: LaunchRepository
) {
    operator fun invoke(forceRefresh: Boolean): Flow<DomainResult<List<Launch>>> {
        return repository.getLastLaunches(forceRefresh).map { result ->
            if (result is DomainResult.Success) {
                val sortedList = result.data.sortedByDescending { it.launchDate }
                DomainResult.Success(sortedList)
            } else {
                result
            }
        }
    }
}
