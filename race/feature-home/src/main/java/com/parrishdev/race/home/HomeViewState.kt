package com.parrishdev.race.home

import com.parrishdev.race.model.Race

/**
 * UI-ready state for HomeScreen.
 *
 * Contains only data needed by the UI to render.
 * NO derived properties - only constructor properties.
 */
data class HomeViewState(
    val isLoading: Boolean,
    val isRefreshing: Boolean,
    val races: List<Race>,
    val errorMessage: String?,
    val showEmptyState: Boolean
)
