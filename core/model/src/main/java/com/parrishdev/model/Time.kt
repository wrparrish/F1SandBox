package com.parrishdev.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Time(@Json(name = "time")
                val time: String = "")