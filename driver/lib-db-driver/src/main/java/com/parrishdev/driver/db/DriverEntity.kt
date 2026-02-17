package com.parrishdev.driver.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing driver data.
 * Includes championship standings when available.
 */
@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey
    val id: String, // Composite: "{season}_{driverNumber}"
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
    val wins: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    companion object {
        fun createId(season: Int, driverNumber: Int): String = "${season}_$driverNumber"
    }
}
