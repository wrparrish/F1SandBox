package com.parrishdev.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FastestLap(@Json(name = "AverageSpeed")
                      val averageSpeed: AverageSpeed? = null,
                      @Json(name = "rank")
                      val rank: String = "",
                      @Json(name = "lap")
                      val lap: String = "",
                      @Json(name = "Time")
                      val time: Time)