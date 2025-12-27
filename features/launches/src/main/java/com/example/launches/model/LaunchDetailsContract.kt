package com.example.launches.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
sealed interface LaunchDetailsUiState {
    data object Loading : LaunchDetailsUiState

    @Immutable
    data class Success(val launch: LaunchUiModel) : LaunchDetailsUiState

    @Immutable
    data class Error(val message: String) : LaunchDetailsUiState
}

sealed interface LaunchDetailsIntent {
    data object Retry : LaunchDetailsIntent
    data object BackClicked : LaunchDetailsIntent
}

sealed interface LaunchDetailsUiEffect {
    data object NavigateBack : LaunchDetailsUiEffect
}
