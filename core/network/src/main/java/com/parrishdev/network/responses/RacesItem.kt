package com.parrishdev.network.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RacesItem(@Json(name = "date")
                     val date: String = "",
                     @Json(name = "round")
                     val round: String = "",
                     @Json(name = "Results")
                     val results: List<ResultsItem>?,
                     @Json(name = "season")
                     val season: String = "",
                     @Json(name = "raceName")
                     val raceName: String = "",
                     @Json(name = "Circuit")
                     val circuit: Circuit,
                     @Json(name = "time")
                     val time: String = "",
                     @Json(name = "url")
                     val url: String = "")