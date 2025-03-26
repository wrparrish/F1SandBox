package com.parrishdev.network

import com.parrishdev.model.Driver
import com.parrishdev.model.Meeting
import com.parrishdev.model.RaceResultsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface F1Endpoint {
    @GET("drivers")
    suspend fun getDrivers(@Query("session_key") sessionKey: String = "9158"): List<Driver>

    @GET("meetings")
    suspend fun getMeetings(@Query("year") yearToFetchFor: String = "2025"): List<Meeting>
}

interface  ErgastEndpoint {
    @GET("2025/results/")
    suspend fun getResults(): RaceResultsResponse
}
