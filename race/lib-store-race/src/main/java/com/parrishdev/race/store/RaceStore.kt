package com.parrishdev.race.store

import com.parrishdev.race.model.Race
import com.parrishdev.race.model.RaceWithResults
import kotlinx.coroutines.flow.Flow

/**
 * Store interface for race data.
 *
 * Follows the pattern: UI observes reactive Flows, Store handles
 * caching logic and network refresh.
 *
 * Data flow: Network → Database → UI (single source of truth)
 */
interface RaceStore {

    /**
     * Stream all races for a given season.
     * Returns data from local database, automatically updated when refresh completes.
     *
     * @param season The season year (e.g., 2025)
     * @return Flow of races, sorted by round
     */
    fun streamRacesForSeason(season: Int): Flow<List<Race>>

    /**
     * Stream a specific race with its results.
     *
     * @param season The season year
     * @param round The round number
     * @return Flow of race with results, or null if not found
     */
    fun streamRaceWithResults(season: Int, round: Int): Flow<RaceWithResults?>

    /**
     * Refresh race data from the network.
     * Data is saved to database; observers will automatically receive updates.
     *
     * @param season The season year
     * @param force If true, ignores staleness check and always fetches
     */
    suspend fun refreshRaces(season: Int, force: Boolean = false)

    /**
     * Refresh results for a specific race from the network.
     *
     * @param season The season year
     * @param round The round number
     * @param force If true, ignores staleness check and always fetches
     */
    suspend fun refreshRaceResults(season: Int, round: Int, force: Boolean = false)
}
