package com.parrishdev.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Meeting(
    @Json(name = "meeting_official_name")
    val meetingOfficialName: String = "",
    @Json(name = "year")
    val year: Int = 0,
    @Json(name = "meeting_key")
    val meetingKey: Int = 0,
    @Json(name = "country_code")
    val countryCode: String = "",
    @Json(name = "date_start")
    val dateStart: String = "",
    @Json(name = "country_key")
    val countryKey: Int = 0,
    @Json(name = "circuit_short_name")
    val circuitShortName: String = "",
    @Json(name = "country_name")
    val countryName: String = "",
    @Json(name = "circuit_key")
    val circuitKey: Int = 0,
    @Json(name = "location")
    val location: String = "",
    @Json(name = "meeting_name")
    val meetingName: String = "",
    @Json(name = "gmt_offset")
    val gmtOffset: String = "",
    @Json(name = "meeting_code")
    val meetingCode: String = ""
)