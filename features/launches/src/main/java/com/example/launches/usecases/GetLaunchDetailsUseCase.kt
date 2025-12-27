package com.example.launches.usecases

import com.example.domain.models.Launch
import com.example.domain.repository.LaunchRepository
import com.example.domain.result.DomainResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLaunchDetailsUseCase @Inject constructor(
    private val launchesRepository: LaunchRepository
) {
    /**
     * Recupera i dettagli di un lancio specifico.
     * Restituisce un Flow per essere reattivi a cambiamenti nel DB (single source of truth).
     */
    operator fun invoke(launchId: String): Flow<DomainResult<Launch>> {
        return launchesRepository.getLaunchDetails(launchId)
    }
}
