package com.parrishdev.race.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for storing race result data.
 */
@Entity(
    tableName = "race_results",
    foreignKeys = [
        ForeignKey(
            entity = RaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["raceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("raceId")]
)
data class RaceResultEntity(
    @PrimaryKey
    val id: String, // Composite: "{raceId}_{driverId}"
    val raceId: String,
    val position: Int,
    val positionText: String,
    val points: Float,
    val driverId: String,
    val driverPermanentNumber: String,
    val driverCode: String,
    val driverGivenName: String,
    val driverFamilyName: String,
    val driverNationality: String,
    val driverUrl: String,
    val constructorId: String,
    val constructorName: String,
    val constructorNationality: String,
    val constructorUrl: String,
    val grid: Int,
    val laps: Int,
    val status: String,
    val fastestLapRank: Int?,
    val fastestLapLap: Int?,
    val fastestLapTime: String?,
    val fastestLapSpeedUnits: String?,
    val fastestLapSpeed: String?,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    companion object {
        fun createId(raceId: String, driverId: String): String = "${raceId}_$driverId"
    }
}
