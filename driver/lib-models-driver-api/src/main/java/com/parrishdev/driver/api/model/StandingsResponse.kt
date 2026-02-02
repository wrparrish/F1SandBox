package com.parrishdev.driver.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Root response from Jolpica API for driver standings.
 * Example endpoint: https://api.jolpi.ca/ergast/f1/2025/driverStandings.json
 */
@JsonClass(generateAdapter = true)
data class StandingsResponse(
    @Json(name = "MRData")
    val data: StandingsMRDataDto
)

@JsonClass(generateAdapter = true)
data class StandingsMRDataDto(
    @Json(name = "xmlns")
    val xmlns: String = "",
    @Json(name = "series")
    val series: String = "",
    @Json(name = "url")
    val url: String = "",
    @Json(name = "limit")
    val limit: String = "",
    @Json(name = "offset")
    val offset: String = "",
    @Json(name = "total")
    val total: String = "",
    @Json(name = "StandingsTable")
    val standingsTable: StandingsTableDto
)

@JsonClass(generateAdapter = true)
data class StandingsTableDto(
    @Json(name = "season")
    val season: String = "",
    @Json(name = "StandingsLists")
    val standingsLists: List<StandingsListDto>? = null
)

@JsonClass(generateAdapter = true)
data class StandingsListDto(
    @Json(name = "season")
    val season: String = "",
    @Json(name = "round")
    val round: String = "",
    @Json(name = "DriverStandings")
    val driverStandings: List<DriverStandingDto>? = null
)

@JsonClass(generateAdapter = true)
data class DriverStandingDto(
    @Json(name = "position")
    val position: String = "",
    @Json(name = "positionText")
    val positionText: String = "",
    @Json(name = "points")
    val points: String = "",
    @Json(name = "wins")
    val wins: String = "",
    @Json(name = "Driver")
    val driver: StandingsDriverDto,
    @Json(name = "Constructors")
    val constructors: List<StandingsConstructorDto>? = null
)

@JsonClass(generateAdapter = true)
data class StandingsDriverDto(
    @Json(name = "driverId")
    val driverId: String = "",
    @Json(name = "permanentNumber")
    val permanentNumber: String? = null,
    @Json(name = "code")
    val code: String? = null,
    @Json(name = "url")
    val url: String = "",
    @Json(name = "givenName")
    val givenName: String = "",
    @Json(name = "familyName")
    val familyName: String = "",
    @Json(name = "dateOfBirth")
    val dateOfBirth: String? = null,
    @Json(name = "nationality")
    val nationality: String = ""
)

@JsonClass(generateAdapter = true)
data class StandingsConstructorDto(
    @Json(name = "constructorId")
    val constructorId: String = "",
    @Json(name = "url")
    val url: String = "",
    @Json(name = "name")
    val name: String = "",
    @Json(name = "nationality")
    val nationality: String = ""
)
