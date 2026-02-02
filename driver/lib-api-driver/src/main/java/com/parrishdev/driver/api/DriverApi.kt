package com.parrishdev.driver.api

import com.parrishdev.driver.api.model.DriverDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for the OpenF1 API - Driver endpoints.
 * Base URL: https://api.openf1.org/v1/
 */
interface DriverApi {

    /**
     * Get all drivers for a session.
     * @param sessionKey The session key. Use "latest" to get drivers from the most recent session.
     */
    @GET("drivers")
    suspend fun getDrivers(
        @Query("session_key") sessionKey: String = "latest"
    ): List<DriverDto>
}
