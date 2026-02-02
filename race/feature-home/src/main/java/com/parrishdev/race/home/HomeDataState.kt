package com.parrishdev.race.home

import com.parrishdev.race.model.Race

/**
 * Internal data state for [HomeViewModel].
 *
 * Contains raw data from stores and derived properties for business logic.
 * Nullable properties indicate loading state (null = loading).
 */
data class HomeDataState(
    val races: List<Race>? = null,
    val isRefreshing: Boolean = false,
    val error: String? = null
) {
    /**
     * True if initial data is still loading (races is null and no error).
     */
    val isLoading: Boolean
        get() = races == null && error == null

    /**
     * True if there's data to display.
     */
    val hasData: Boolean
        get() = !races.isNullOrEmpty()

    /**
     * Returns the current season year (derived from first race, or current year).
     */
    val season: Int
        get() = races?.firstOrNull()?.season ?: java.time.Year.now().value
}
