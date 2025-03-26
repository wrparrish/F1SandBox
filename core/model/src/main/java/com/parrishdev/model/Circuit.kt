package com.parrishdev.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Circuit(@Json(name = "circuitId")
                   val circuitId: String = "",
                   @Json(name = "url")
                   val url: String = "",
                   @Json(name = "circuitName")
                   val circuitName: String = "",
                   @Json(name = "Location")
                   val location: Location)