package com.parrishdev.network.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RaceTable(@Json(name = "Races")
                     val races: List<RacesItem>?,
                     @Json(name = "round")
                     val round: String = "",
                     @Json(name = "season")
                     val season: String = "")