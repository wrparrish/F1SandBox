package com.parrishdev.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AverageSpeed(@Json(name = "units")
                        val units: String = "",
                        @Json(name = "speed")
                        val speed: String = "")