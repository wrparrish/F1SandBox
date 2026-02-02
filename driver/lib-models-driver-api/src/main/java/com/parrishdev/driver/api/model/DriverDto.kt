package com.parrishdev.driver.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * API DTO for driver data from OpenF1 API.
 * Uses snake_case field names matching the API response.
 */
@JsonClass(generateAdapter = true)
data class DriverDto(
    @Json(name = "driver_number")
    val driverNumber: Int?,
    @Json(name = "first_name")
    val firstName: String?,
    @Json(name = "last_name")
    val lastName: String?,
    @Json(name = "full_name")
    val fullName: String?,
    @Json(name = "name_acronym")
    val nameAcronym: String?,
    @Json(name = "country_code")
    val countryCode: String?,
    @Json(name = "team_name")
    val teamName: String?,
    @Json(name = "team_colour")
    val teamColour: String?,
    @Json(name = "headshot_url")
    val headshotUrl: String?,
    @Json(name = "broadcast_name")
    val broadcastName: String?,
    @Json(name = "session_key")
    val sessionKey: Int?,
    @Json(name = "meeting_key")
    val meetingKey: Int?
)
