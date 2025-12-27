package com.example.launches.model

import androidx.compose.runtime.Stable
import com.example.domain.models.Launch
import com.example.domain.result.DataError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
sealed interface LaunchesUiState {
    data object Loading : LaunchesUiState

    @Stable
    data class Success(val launches: ImmutableList<Launch>) : LaunchesUiState

    @Stable
    data class Error(
        val error: DataError,
        val launches: ImmutableList<Launch> = persistentListOf()
    ) : LaunchesUiState
}
