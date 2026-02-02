package com.parrishdev.race.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for race-related database operations.
 */
@Dao
interface RaceDao {

    // ==================== Race Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRace(race: RaceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRaces(races: List<RaceEntity>)

    @Query("SELECT * FROM races WHERE season = :season ORDER BY round ASC")
    fun getRacesForSeason(season: Int): Flow<List<RaceEntity>>

    @Query("SELECT * FROM races WHERE id = :raceId")
    fun getRaceById(raceId: String): Flow<RaceEntity?>

    @Query("SELECT * FROM races WHERE season = :season AND round = :round")
    fun getRaceBySeasonAndRound(season: Int, round: Int): Flow<RaceEntity?>

    @Query("SELECT lastUpdated FROM races WHERE season = :season ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLastUpdatedForSeason(season: Int): Long?

    @Query("DELETE FROM races WHERE season = :season")
    suspend fun deleteRacesForSeason(season: Int)

    // ==================== Race Result Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRaceResults(results: List<RaceResultEntity>)

    @Query("SELECT * FROM race_results WHERE raceId = :raceId ORDER BY position ASC")
    fun getResultsForRace(raceId: String): Flow<List<RaceResultEntity>>

    @Query("DELETE FROM race_results WHERE raceId = :raceId")
    suspend fun deleteResultsForRace(raceId: String)

    // ==================== Combined Operations ====================

    @Transaction
    suspend fun insertRaceWithResults(race: RaceEntity, results: List<RaceResultEntity>) {
        insertRace(race)
        deleteResultsForRace(race.id)
        insertRaceResults(results)
    }

    @Transaction
    suspend fun insertRacesWithResults(racesWithResults: List<Pair<RaceEntity, List<RaceResultEntity>>>) {
        racesWithResults.forEach { (race, results) ->
            insertRace(race)
            deleteResultsForRace(race.id)
            insertRaceResults(results)
        }
    }
}
