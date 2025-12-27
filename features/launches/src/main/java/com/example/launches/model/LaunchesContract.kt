package com.example.launches.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.example.domain.result.DataError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
sealed interface LaunchesUiState {
    data object Loading : LaunchesUiState

    @Immutable
    data class Success(val launches: ImmutableList<LaunchUiModel>) : LaunchesUiState

    @Immutable
    data class Error(
        val error: DataError,
        val launches: ImmutableList<LaunchUiModel> = persistentListOf()
    ) : LaunchesUiState
}

sealed interface LaunchesIntent {
    data object Refresh : LaunchesIntent
    data class LaunchClicked(val id: String) : LaunchesIntent
}

sealed interface LaunchesUiEffect {
    data class NavigateToDetail(val launchId: String) : LaunchesUiEffect
    data class ShowToast(val message: String) : LaunchesUiEffect
}
