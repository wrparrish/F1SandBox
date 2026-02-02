package com.parrishdev.race.api

import com.parrishdev.race.api.model.RaceResultsResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit interface for the Jolpica F1 API - Race endpoints.
 * Base URL: https://api.jolpi.ca/ergast/f1/
 *
 * Note: Jolpica is the successor to the deprecated Ergast API and is
 * backward-compatible with the same response format.
 */
interface RaceApi {

    /**
     * Get race schedule for a season (without results).
     * Returns all races for the season with circuit/date info but no driver results.
     *
     * Use this for displaying the race list on the home screen.
     *
     * @param season The season year (e.g., "2025")
     */
    @GET("{season}.json")
    suspend fun getRaceSchedule(
        @Path("season") season: String
    ): RaceResultsResponse

    /**
     * Get results for a specific race.
     * Returns full race details including all driver results, positions, times, etc.
     *
     * Use this when viewing a specific race's results.
     *
     * @param season The season year
     * @param round The round number
     */
    @GET("{season}/{round}/results.json")
    suspend fun getRaceResultsByRound(
        @Path("season") season: String,
        @Path("round") round: String
    ): RaceResultsResponse
}
