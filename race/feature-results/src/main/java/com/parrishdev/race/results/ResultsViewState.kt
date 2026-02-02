package com.parrishdev.race.results

import com.parrishdev.race.model.RaceResult

/**
 * UI-ready state for ResultsScreen.
 */
data class ResultsViewState(
    val isLoading: Boolean,
    val isRefreshing: Boolean,
    val raceName: String,
    val circuitName: String,
    val date: String,
    val results: List<RaceResult>,
    val errorMessage: String?
)
