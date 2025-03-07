package com.parrishdev.network

import com.parrishdev.model.Driver
import retrofit2.http.GET
import retrofit2.http.Query

interface F1Endpoint {
    @GET("drivers")
    suspend fun getDrivers(@Query("session_key") sessionKey: String = "9158"): List<Driver>
}
