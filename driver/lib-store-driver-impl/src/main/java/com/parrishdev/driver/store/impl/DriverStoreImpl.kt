package com.parrishdev.driver.store.impl

import android.util.Log
import com.parrishdev.common.udf.DispatcherProvider
import com.parrishdev.driver.api.DriverApi
import com.parrishdev.driver.api.StandingsApi
import com.parrishdev.driver.api.model.DriverDto
import com.parrishdev.driver.api.model.DriverStandingDto
import com.parrishdev.driver.db.DriverDao
import com.parrishdev.driver.db.DriverEntity
import com.parrishdev.driver.model.Driver
import com.parrishdev.driver.store.DriverStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.io.IOException
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
    private val clock: Clock,
    private val dispatcherProvider: DispatcherProvider,
    private val driverApi: DriverApi,
    private val standingsApi: StandingsApi,
    private val driverDao: DriverDao
) : DriverStore {

    override fun streamDrivers(sessionKey: Int): Flow<List<Driver>> {
        return driverDao.getDriversForSession(sessionKey)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun streamDriver(driverNumber: Int, season: Int): Flow<Driver?> {
        return driverDao.getDriverByNumber(driverNumber, season)
            .map { entity -> entity?.toDomain() }
    }

    override fun streamDriversForSeason(season: Int): Flow<List<Driver>> {
        return driverDao.getDriversForSeason(season)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun refreshDrivers(sessionKey: Int, force: Boolean) {
        withContext(dispatcherProvider.io) {
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
            // Use current year as fallback season for raw sessions
            val currentYear = java.time.Year.now().value
            val driverEntities = driverDtos.mapNotNull { it.toEntity(currentYear) }

            if (driverEntities.isNotEmpty()) {
                // Insert into database (Flow will auto-emit)
                driverDao.insertDrivers(driverEntities)
            }
        }
    }

    override suspend fun refreshLatestDrivers(season: Int, force: Boolean) {
        withContext(dispatcherProvider.io) {
            // Check staleness using any cached driver's lastUpdated for this season
            if (!force) {
                val lastUpdated = driverDao.getLatestUpdateTime(season)
                if (lastUpdated != null && !isStale(lastUpdated)) {
                    return@withContext
                }
            }

            // Fetch drivers from OpenF1 (has headshots, team colors)
            val driverResult = runCatching {
                driverApi.getDrivers()
            }

            // Fetch standings from Jolpica (has points, position)
            val standingsResult = runCatching {
                fetchStandingsForSeason(season.toString())
            }

            val standings = standingsResult.getOrNull()
            val drivers: List<DriverDto?>? = driverResult.getOrNull()

            if (standings != null) {
                if (drivers != null) {
                    // Transform and filter, merging standings data
                    val driverEntities = drivers.mapNotNull { dto ->
                        dto?.toEntity(season)?.let { entity ->
                            mergeWithStandings(entity, standings)
                        }
                    }

                    if (driverEntities.isNotEmpty()) {
                        // Atomically replace all drivers for this season
                        driverDao.replaceDriversForSeason(season, driverEntities)
                    }
                } else {
                    // degraded path: standings only, no headshots/colors.
                    val fallbackEntities = standings.mapNotNull { standingEntry ->
                        val standing = standingEntry.value
                        standing.toEntity(season = season)
                    }
                    driverDao.replaceDriversForSeason(season, fallbackEntities)
                }
            } else {
                throw standingsResult.exceptionOrNull()
                    ?: IOException("Failed to fetch standings")
            }
        }
    }

    private suspend fun fetchStandingsForSeason(season: String): Map<String, DriverStandingDto> {
        val response = standingsApi.getDriverStandings(season)
        val standings = response.data.standingsTable.standingsLists?.firstOrNull()

        val driverStandings = standings
            ?.driverStandings
            ?: emptyList()

        // Create map keyed by driver code for efficient lookup
        return driverStandings.associateBy { standing ->
            standing.driver.code?.uppercase().orEmpty()
        }.filterKeys {
            it.isNotEmpty()
        }
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
        private const val SEASON = 2025

        // Data is stale after 1 hour
        private const val STALE_THRESHOLD_MS = 60 * 60 * 1000L
    }

    private fun isStale(lastUpdated: Long): Boolean {
        return clock.now().toEpochMilliseconds() - lastUpdated > STALE_THRESHOLD_MS
    }
}
