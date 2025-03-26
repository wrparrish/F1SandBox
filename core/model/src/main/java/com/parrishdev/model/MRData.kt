package com.parrishdev.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MRData(
    @Json(name = "RaceTable")
    val raceTable: RaceTable,
    @Json(name = "xmlns")
    val xmlns: String = "",
    @Json(name = "total")
    val total: String = "",
    @Json(name = "offset")
    val offset: String = "",
    @Json(name = "series")
    val series: String = "",
    @Json(name = "limit")
    val limit: String = "",
    @Json(name = "url")
    val url: String = ""
)