package com.parrishdev.network.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DriverStandingsResponse(
    @Json(name = "MRData") val mrData: MRDataStandings
)

// MRData wrapper specific for Standings to avoid conflicts if structure differs from other MRData uses
@JsonClass(generateAdapter = true)
data class MRDataStandings(
    @Json(name = "xmlns") val xmlns: String?,
    @Json(name = "series") val series: String?,
    @Json(name = "url") val url: String?,
    @Json(name = "limit") val limit: String?,
    @Json(name = "offset") val offset: String?,
    @Json(name = "total") val total: String?,
    @Json(name = "StandingsTable") val standingsTable: StandingsTable?
)
