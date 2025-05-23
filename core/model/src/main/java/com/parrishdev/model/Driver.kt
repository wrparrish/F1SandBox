package com.parrishdev.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Driver(
    @Json(name = "driver_number")
    val driverNumber: Int? = null,
    @Json(name = "country_code")
    val countryCode: String? = null,
    @Json(name = "full_name")
    val fullName: String? = null,
    @Json(name = "meeting_key")
    val meetingKey: Int? = null,
    @Json(name = "headshot_url")
    val headshotUrl: String? = null,
    @Json(name = "name_acronym")
    val nameAcronym: String? = null,
    @Json(name = "session_key")
    val sessionKey: Int? = null,
    @Json(name = "last_name")
    val lastName: String? = null,
    @Json(name = "team_colour")
    val teamColour: String? = null,
    @Json(name = "broadcast_name")
    val broadcastName: String? = null,
    @Json(name = "first_name")
    val firstName: String? = null,
    @Json(name = "team_name")
    val teamName: String? = null,
    // Added for driver standings integration
    var points: String = "0",
    var position: String? = null
)