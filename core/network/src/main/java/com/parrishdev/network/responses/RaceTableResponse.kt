package com.parrishdev.network.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RaceTableResponse(
    @Json(name = "MRData") val mrData: MRData // Reusing the existing MRData which contains RaceTable
)
