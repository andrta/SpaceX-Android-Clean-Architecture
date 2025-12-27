package com.example.launches.model

/**
 * MVI: Intent (Input)
 * Represents all the possible actions or intentions the user can trigger within the Launches screen.
 * These events are processed by the ViewModel to update the state or trigger side effects.
 */
sealed interface LaunchesIntent {
    /**
     * Triggered when the user manually requests a data refresh
     * (e.g., by using the Swipe-to-Refresh gesture).
     */
    data object Refresh : LaunchesIntent

    /**
     * Triggered when the user taps on a specific launch item.
     * Carries the [id] of the selected launch to initiate navigation.
     */
    data class LaunchClicked(val id: String) : LaunchesIntent
}

/**
 * MVI: Side Effect (One-Shot Output)
 * Represents "fire-and-forget" events that should be handled only once by the UI.
 * Unlike UiState, these are not persistent and should not be re-emitted upon configuration changes.
 */
sealed interface LaunchesUiEffect {
    /**
     * Instructs the UI to navigate to the Details screen.
     */
    data class NavigateToDetail(val launchId: String) : LaunchesUiEffect

    /**
     * Displays a transient message to the user (e.g., a Toast or Snackbar).
     * Useful for non-blocking errors (e.g., "Refresh failed" while showing old data).
     */
    data class ShowToast(val message: String) : LaunchesUiEffect
}
