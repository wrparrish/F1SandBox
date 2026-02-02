package com.parrishdev.network.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DriverStanding(
    @Json(name = "position") val position: String,
    @Json(name = "positionText") val positionText: String,
    @Json(name = "points") val points: String,
    @Json(name = "wins") val wins: String,
    @Json(name = "Driver") val driver: Driver,
    @Json(name = "Constructors") val constructors: List<Constructor>
)
