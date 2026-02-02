package com.parrishdev.race.store.impl

import com.parrishdev.race.api.RaceApi
import com.parrishdev.race.db.RaceDao
import com.parrishdev.race.db.RaceEntity
import com.parrishdev.race.model.Race
import com.parrishdev.race.model.RaceWithResults
import com.parrishdev.race.store.RaceStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [RaceStore] that manages race data.
 *
 * Data flow:
 * - Network (Jolpica F1 API) → Database (Room) → UI (Flow)
 *
 * Strategy:
 * - Race schedule (list): Fetched via /{season}.json endpoint (fast, all races)
 * - Race results (detail): Fetched on-demand via /{season}/{round}/results.json
 *
 * Staleness:
 * - Race data is considered stale after 1 hour
 */
@Singleton
class RaceStoreImpl @Inject constructor(
    private val raceApi: RaceApi,
    private val raceDao: RaceDao
) : RaceStore {

    companion object {
        // Data is stale after 1 hour
        private const val STALE_THRESHOLD_MS = 60 * 60 * 1000L
    }

    override fun streamRacesForSeason(season: Int): Flow<List<Race>> {
        return raceDao.getRacesForSeason(season)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun streamRaceWithResults(season: Int, round: Int): Flow<RaceWithResults?> {
        val raceId = RaceEntity.createId(season, round)
        return combine(
            raceDao.getRaceById(raceId),
            raceDao.getResultsForRace(raceId)
        ) { raceEntity, resultEntities ->
            raceEntity?.let { race ->
                (race to resultEntities).toDomain()
            }
        }
    }

    override suspend fun refreshRaces(season: Int, force: Boolean) {
        withContext(Dispatchers.IO) {
            // Check staleness
            if (!force) {
                val lastUpdated = raceDao.getLastUpdatedForSeason(season)
                if (lastUpdated != null && !isStale(lastUpdated)) {
                    return@withContext
                }
            }

            // Fetch race schedule from network (without results - faster, gets all races)
            val response = raceApi.getRaceSchedule(season.toString())
            val races = response.data.raceTable.races ?: return@withContext

            // Transform to entities (results will be empty - loaded on-demand)
            val raceEntities = races.map { raceDto -> raceDto.toEntity() }

            // Insert races into database (Flow will auto-emit)
            raceDao.insertRaces(raceEntities)
        }
    }

    override suspend fun refreshRaceResults(season: Int, round: Int, force: Boolean) {
        withContext(Dispatchers.IO) {
            // Fetch specific race results from network
            val response = raceApi.getRaceResultsByRound(season.toString(), round.toString())
            val races = response.data.raceTable.races ?: return@withContext
            val raceDto = races.firstOrNull() ?: return@withContext

            // Transform and save to database
            val raceEntity = raceDto.toEntity()
            val resultEntities = raceDto.results?.map { it.toEntity(raceEntity.id) } ?: emptyList()

            raceDao.insertRaceWithResults(raceEntity, resultEntities)
        }
    }

    private fun isStale(lastUpdated: Long): Boolean {
        return System.currentTimeMillis() - lastUpdated > STALE_THRESHOLD_MS
    }
}
