package com.parrishdev.driver.api

import com.parrishdev.driver.api.model.StandingsResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit interface for the Jolpica F1 API - Driver Standings endpoints.
 * Base URL: https://api.jolpi.ca/ergast/f1/
 */
interface StandingsApi {

    /**
     * Get driver standings for a specific season.
     * @param season The season year (e.g., "2025") or "current" for current season
     */
    @GET("{season}/driverStandings.json")
    suspend fun getDriverStandings(
        @Path("season") season: String
    ): StandingsResponse
}
