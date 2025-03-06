package com.parrishdev.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Driver(
    @Json(name = "driver_number")
    val driverNumber: Int = 0,
    @Json(name = "country_code")
    val countryCode: String = "",
    @Json(name = "full_name")
    val fullName: String = "",
    @Json(name = "meeting_key")
    val meetingKey: Int = 0,
    @Json(name = "headshot_url")
    val headshotUrl: String = "",
    @Json(name = "name_acronym")
    val nameAcronym: String = "",
    @Json(name = "session_key")
    val sessionKey: Int = 0,
    @Json(name = "last_name")
    val lastName: String = "",
    @Json(name = "team_colour")
    val teamColour: String = "",
    @Json(name = "broadcast_name")
    val broadcastName: String = "",
    @Json(name = "first_name")
    val firstName: String = "",
    @Json(name = "team_name")
    val teamName: String = ""
)