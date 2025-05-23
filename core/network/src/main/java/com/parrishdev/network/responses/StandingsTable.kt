package com.parrishdev.network.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StandingsTable(
    @Json(name = "season") val season: String?, // Season can also be at this level
    @Json(name = "StandingsLists") val standingsLists: List<StandingsList>
)
