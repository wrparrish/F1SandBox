package com.parrishdev.network.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StandingsList(
    @Json(name = "season") val season: String?, // Season might be at this level too
    @Json(name = "round") val round: String?,   // Round might be at this level
    @Json(name = "DriverStandings") val driverStandings: List<DriverStanding>
)
