package com.parrishdev.race.results

import com.parrishdev.race.model.RaceWithResults

/**
 * Internal data state for [ResultsViewModel].
 */
data class ResultsDataState(
    val season: Int,
    val round: Int,
    val raceWithResults: RaceWithResults? = null,
    val isRefreshing: Boolean = false,
    val error: String? = null
) {
    val isLoading: Boolean
        get() = raceWithResults == null && error == null

    val hasData: Boolean
        get() = raceWithResults != null
}
