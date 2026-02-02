package com.parrishdev.race.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Root response from Ergast API for race results.
 * Example endpoint: https://api.jolpi.ca/ergast/f1/2025/results/
 */
@JsonClass(generateAdapter = true)
data class RaceResultsResponse(
    @Json(name = "MRData")
    val data: MRDataDto
)

@JsonClass(generateAdapter = true)
data class MRDataDto(
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
    @Json(name = "RaceTable")
    val raceTable: RaceTableDto
)

@JsonClass(generateAdapter = true)
data class RaceTableDto(
    @Json(name = "season")
    val season: String = "",
    @Json(name = "Races")
    val races: List<RaceDto>?
)
