package com.parrishdev.race.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO for a race result entry from Ergast API.
 */
@JsonClass(generateAdapter = true)
data class ResultDto(
    @Json(name = "number")
    val number: String = "",
    @Json(name = "position")
    val position: String = "",
    @Json(name = "positionText")
    val positionText: String = "",
    @Json(name = "points")
    val points: String = "",
    @Json(name = "Driver")
    val driver: DriverDto,
    @Json(name = "Constructor")
    val constructor: ConstructorDto,
    @Json(name = "grid")
    val grid: String = "",
    @Json(name = "laps")
    val laps: String = "",
    @Json(name = "status")
    val status: String = "",
    @Json(name = "FastestLap")
    val fastestLap: FastestLapDto? = null
)

/**
 * DTO for a driver from Ergast API (race results context).
 */
@JsonClass(generateAdapter = true)
data class DriverDto(
    @Json(name = "driverId")
    val driverId: String = "",
    @Json(name = "permanentNumber")
    val permanentNumber: String = "",
    @Json(name = "code")
    val code: String = "",
    @Json(name = "url")
    val url: String = "",
    @Json(name = "givenName")
    val givenName: String = "",
    @Json(name = "familyName")
    val familyName: String = "",
    @Json(name = "dateOfBirth")
    val dateOfBirth: String = "",
    @Json(name = "nationality")
    val nationality: String = ""
)

/**
 * DTO for a constructor (team) from Ergast API.
 */
@JsonClass(generateAdapter = true)
data class ConstructorDto(
    @Json(name = "constructorId")
    val constructorId: String = "",
    @Json(name = "url")
    val url: String = "",
    @Json(name = "name")
    val name: String = "",
    @Json(name = "nationality")
    val nationality: String = ""
)

/**
 * DTO for fastest lap information from Ergast API.
 */
@JsonClass(generateAdapter = true)
data class FastestLapDto(
    @Json(name = "rank")
    val rank: String = "",
    @Json(name = "lap")
    val lap: String = "",
    @Json(name = "Time")
    val time: TimeDto? = null,
    @Json(name = "AverageSpeed")
    val averageSpeed: AverageSpeedDto? = null
)

/**
 * DTO for lap time from Ergast API.
 */
@JsonClass(generateAdapter = true)
data class TimeDto(
    @Json(name = "time")
    val time: String = ""
)

/**
 * DTO for average speed from Ergast API.
 */
@JsonClass(generateAdapter = true)
data class AverageSpeedDto(
    @Json(name = "units")
    val units: String = "",
    @Json(name = "speed")
    val speed: String = ""
)
