package com.parrishdev.race.home

/**
 * One-time events emitted by [HomeViewModel].
 * These are consumed by the UI for navigation, toasts, etc.
 */
sealed interface HomeEvent {

    /**
     * Navigate to race results detail screen.
     */
    data class NavigateToRaceResults(
        val season: Int,
        val round: Int,
        val raceName: String
    ) : HomeEvent

    /**
     * Show an error message (snackbar/toast).
     */
    data class ShowError(val message: String) : HomeEvent
}
