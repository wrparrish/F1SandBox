package com.parrishdev.driver.store.impl

import android.util.Log
import com.parrishdev.driver.api.DriverApi
import com.parrishdev.driver.api.StandingsApi
import com.parrishdev.driver.api.model.DriverStandingDto
import com.parrishdev.driver.db.DriverDao
import com.parrishdev.driver.db.DriverEntity
import com.parrishdev.driver.model.Driver
import com.parrishdev.driver.store.DriverStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [DriverStore] that manages driver data.
 *
 * Data flow:
 * - Network (OpenF1 for drivers, Jolpica for standings) -> Database (Room) -> UI (Flow)
 * - Drivers are matched by name acronym (code) to merge standings data
 *
 * Staleness:
 * - Driver data is considered stale after 1 hour
 */
@Singleton
class DriverStoreImpl @Inject constructor(
    private val driverApi: DriverApi,
    private val standingsApi: StandingsApi,
    private val driverDao: DriverDao
) : DriverStore {

    override fun streamDrivers(sessionKey: Int): Flow<List<Driver>> {
        return driverDao.getDriversForSession(sessionKey)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun streamDriver(driverNumber: Int): Flow<Driver?> {
        return driverDao.getDriverByNumber(driverNumber)
            .map { entity -> entity?.toDomain() }
    }

    override fun streamAllDrivers(): Flow<List<Driver>> {
        return driverDao.getAllDrivers()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun refreshDrivers(sessionKey: Int, force: Boolean) {
        withContext(Dispatchers.IO) {
            // Check staleness
            if (!force) {
                val lastUpdated = driverDao.getLastUpdatedForSession(sessionKey)
                if (lastUpdated != null && !isStale(lastUpdated)) {
                    return@withContext
                }
            }

            // Fetch from network
            val driverDtos = driverApi.getDrivers(sessionKey.toString())

            // Transform and filter (skip entries without team name)
            val driverEntities = driverDtos.mapNotNull { it.toEntity() }

            if (driverEntities.isNotEmpty()) {
                // Insert into database (Flow will auto-emit)
                driverDao.insertDrivers(driverEntities)
            }
        }
    }

    override suspend fun refreshLatestDrivers(force: Boolean) {
        withContext(Dispatchers.IO) {
            // Check staleness using any cached driver's lastUpdated
            if (!force) {
                val lastUpdated = driverDao.getLatestUpdateTime()
                if (lastUpdated != null && !isStale(lastUpdated)) {
                    return@withContext
                }
            }

            // Fetch drivers from OpenF1 (has headshots, team colors)
            val driverDtos = driverApi.getDrivers("latest")

            // Fetch standings from Jolpica (has points, position)
            val standingsMap = fetchCurrentStandings()

            // Transform and filter, merging standings data
            val driverEntities = driverDtos.mapNotNull { dto ->
                dto.toEntity()?.let { entity ->
                    mergeWithStandings(entity, standingsMap)
                }
            }

            if (driverEntities.isNotEmpty()) {
                // Atomically replace all drivers to ensure current season only
                driverDao.replaceAllDrivers(driverEntities)
            }
        }
    }

    /**
     * Fetches the most recent season standings from Jolpica API.
     * Tries current year first, falls back to previous year if no data.
     * Returns a map of driver code (e.g., "VER", "HAM") to their standing data.
     */
    private suspend fun fetchCurrentStandings(): Map<String, DriverStandingDto> {
        return try {
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

            // Try current year first
            var standings = fetchStandingsForSeason(currentYear.toString())

            // If no standings for current year, try previous year
            if (standings.isEmpty()) {
                Log.d(TAG, "No standings for $currentYear, trying ${currentYear - 1}")
                standings = fetchStandingsForSeason((currentYear - 1).toString())
            }

            standings
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch standings, using defaults", e)
            emptyMap()
        }
    }

    private suspend fun fetchStandingsForSeason(season: String): Map<String, DriverStandingDto> {
        val response = standingsApi.getDriverStandings(season)
        val standings = response.data.standingsTable.standingsLists
            ?.firstOrNull()
            ?.driverStandings
            ?: emptyList()

        // Create map keyed by driver code for efficient lookup
        return standings.associateBy { standing ->
            standing.driver.code?.uppercase() ?: ""
        }.filterKeys { it.isNotEmpty() }
    }

    /**
     * Merges driver entity with standings data if available.
     * Matches by driver name acronym (code).
     */
    private fun mergeWithStandings(
        entity: DriverEntity,
        standingsMap: Map<String, DriverStandingDto>
    ): DriverEntity {
        val standing = standingsMap[entity.nameAcronym.uppercase()]
        return if (standing != null) {
            entity.copy(
                championshipPosition = standing.position.toIntOrNull() ?: 0,
                championshipPoints = standing.points.toFloatOrNull() ?: 0f,
                wins = standing.wins.toIntOrNull() ?: 0
            )
        } else {
            entity
        }
    }

    companion object {
        private const val TAG = "DriverStoreImpl"
        // Data is stale after 1 hour
        private const val STALE_THRESHOLD_MS = 60 * 60 * 1000L
    }

    private fun isStale(lastUpdated: Long): Boolean {
        return System.currentTimeMillis() - lastUpdated > STALE_THRESHOLD_MS
    }
}
