package com.parrishdev.network.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResultsItem(@Json(name = "number")
                       val number: String = "",
                       @Json(name = "positionText")
                       val positionText: String = "",
                       @Json(name = "FastestLap")
                       val fastestLap: FastestLap? = null,
                       @Json(name = "Constructor")
                       val constructor: Constructor,
                       @Json(name = "grid")
                       val grid: String = "",
                       @Json(name = "Driver")
                       val driver: Driver,
                       @Json(name = "laps")
                       val laps: String = "",
                       @Json(name = "position")
                       val position: String = "",
                       @Json(name = "points")
                       val points: String = "",
                       @Json(name = "status")
                       val status: String = "")