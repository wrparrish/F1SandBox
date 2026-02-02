package com.parrishdev.race.model

/**
 * Domain model combining a race with its results.
 * This is the primary model used by the UI to display race information.
 */
data class RaceWithResults(
    val race: Race,
    val results: List<RaceResult>
) {
    /**
     * Returns the winner of the race, or null if no results.
     */
    val winner: RaceResult?
        get() = results.firstOrNull { it.position == 1 }

    /**
     * Returns the podium finishers (P1, P2, P3).
     */
    val podium: List<RaceResult>
        get() = results.filter { it.position in 1..3 }.sortedBy { it.position }
}
