package com.parrishdev.race.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO for a race from Jolpica F1 API.
 *
 * Note: circuit and time are nullable for defensive handling of malformed API responses
 * or future races where time is not yet set.
 */
@JsonClass(generateAdapter = true)
data class RaceDto(
    @Json(name = "season")
    val season: String = "",
    @Json(name = "round")
    val round: String = "",
    @Json(name = "url")
    val url: String = "",
    @Json(name = "raceName")
    val raceName: String = "",
    @Json(name = "Circuit")
    val circuit: CircuitDto? = null,
    @Json(name = "date")
    val date: String = "",
    @Json(name = "time")
    val time: String? = null,
    @Json(name = "Results")
    val results: List<ResultDto>?
)

/**
 * DTO for a circuit from Jolpica F1 API.
 */
@JsonClass(generateAdapter = true)
data class CircuitDto(
    @Json(name = "circuitId")
    val circuitId: String = "",
    @Json(name = "url")
    val url: String = "",
    @Json(name = "circuitName")
    val circuitName: String = "",
    @Json(name = "Location")
    val location: LocationDto? = null
)

/**
 * DTO for a location from Jolpica F1 API.
 */
@JsonClass(generateAdapter = true)
data class LocationDto(
    @Json(name = "lat")
    val lat: String = "",
    @Json(name = "long")
    val long: String = "",
    @Json(name = "locality")
    val locality: String = "",
    @Json(name = "country")
    val country: String = ""
)
