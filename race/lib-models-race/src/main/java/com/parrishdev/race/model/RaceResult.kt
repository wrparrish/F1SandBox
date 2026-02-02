package com.parrishdev.race.model

/**
 * Domain model for a race result entry.
 * Represents one driver's result in a race.
 */
data class RaceResult(
    val position: Int,
    val positionText: String,
    val points: Float,
    val driver: RaceDriver,
    val constructor: Constructor,
    val grid: Int,
    val laps: Int,
    val status: String,
    val fastestLap: FastestLap?
)

/**
 * Domain model for a driver in race results context.
 * Contains Ergast-style driver information.
 */
data class RaceDriver(
    val id: String,
    val permanentNumber: String,
    val code: String,
    val givenName: String,
    val familyName: String,
    val nationality: String,
    val url: String
) {
    val fullName: String
        get() = "$givenName $familyName"
}

/**
 * Domain model for a constructor (team).
 */
data class Constructor(
    val id: String,
    val name: String,
    val nationality: String,
    val url: String
)

/**
 * Domain model for fastest lap information.
 */
data class FastestLap(
    val rank: Int,
    val lap: Int,
    val time: String,
    val averageSpeed: AverageSpeed?
)

/**
 * Domain model for average speed.
 */
data class AverageSpeed(
    val units: String,
    val speed: String
)
