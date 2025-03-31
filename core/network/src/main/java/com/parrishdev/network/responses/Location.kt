package com.parrishdev.network.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Location(@Json(name = "country")
                    val country: String = "",
                    @Json(name = "locality")
                    val locality: String = "",
                    @Json(name = "lat")
                    val lat: String = "",
                    @Json(name = "long")
                    val long: String = "")