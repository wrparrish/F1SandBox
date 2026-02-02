package com.parrishdev.race.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing race data.
 */
@Entity(tableName = "races")
data class RaceEntity(
    @PrimaryKey
    val id: String, // Composite: "{season}_{round}"
    val season: Int,
    val round: Int,
    val name: String,
    val date: String,
    val time: String,
    val circuitId: String,
    val circuitName: String,
    val circuitUrl: String,
    val locality: String,
    val country: String,
    val latitude: String,
    val longitude: String,
    val url: String,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    companion object {
        fun createId(season: Int, round: Int): String = "${season}_$round"
    }
}
