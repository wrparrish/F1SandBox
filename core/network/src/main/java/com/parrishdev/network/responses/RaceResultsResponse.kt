package com.parrishdev.network.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RaceResultsResponse(@Json(name = "MRData")
                               val data: MRData)