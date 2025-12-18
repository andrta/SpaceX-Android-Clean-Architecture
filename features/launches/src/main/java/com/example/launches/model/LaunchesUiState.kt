package com.example.launches.model

import androidx.compose.runtime.Immutable
import com.example.domain.models.Launch
import com.example.domain.result.DataError

// Usage of Immutable for Compose performance optimization
// (In a real project, you might annotate this with @Immutable)
@Immutable
sealed interface LaunchesUiState {
    data object Loading : LaunchesUiState
    data class Success(
        val launches: List<Launch>,
        val isRefreshing: Boolean = false // Useful for SwipeRefreshLayout logic
    ) : LaunchesUiState

    data class Error(
        val error: DataError,
        val launches: List<Launch> = emptyList()  // We might keep previous data visible while showing an error (Toast)
    ) : LaunchesUiState
}
