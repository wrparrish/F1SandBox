package com.parrishdev.driver.model

/**
 * Domain model for a Formula 1 driver.
 * Clean Kotlin data class with no serialization concerns.
 *
 * Includes championship standings data (points, position) when available.
 */
data class Driver(
    val driverNumber: Int,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val nameAcronym: String,
    val countryCode: String,
    val teamName: String,
    val teamColour: String,
    val headshotUrl: String?,
    val broadcastName: String,
    val sessionKey: Int,
    val championshipPosition: Int = 0,
    val championshipPoints: Float = 0f,
    val wins: Int = 0
)
