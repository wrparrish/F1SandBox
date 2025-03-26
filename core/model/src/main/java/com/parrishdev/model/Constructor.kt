package com.parrishdev.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Constructor(@Json(name = "nationality")
                       val nationality: String = "",
                       @Json(name = "name")
                       val name: String = "",
                       @Json(name = "constructorId")
                       val constructorId: String = "",
                       @Json(name = "url")
                       val url: String = "")