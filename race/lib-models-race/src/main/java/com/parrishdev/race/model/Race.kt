package com.parrishdev.race.model

/**
 * Domain model for a Formula 1 race.
 * Clean Kotlin data class with no serialization concerns.
 */
data class Race(
    val id: String,
    val season: Int,
    val round: Int,
    val name: String,
    val date: String,
    val time: String,
    val circuit: Circuit,
    val url: String
)

/**
 * Domain model for a race circuit.
 */
data class Circuit(
    val id: String,
    val name: String,
    val location: Location,
    val url: String
)

/**
 * Domain model for a location.
 */
data class Location(
    val locality: String,
    val country: String,
    val latitude: String,
    val longitude: String
)
