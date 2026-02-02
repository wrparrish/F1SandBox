package com.parrishdev.driver.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for driver-related database operations.
 */
@Dao
interface DriverDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriver(driver: DriverEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrivers(drivers: List<DriverEntity>)

    /**
     * Replaces all drivers atomically - deletes existing and inserts new.
     * Used when refreshing from "latest" session to ensure current season only.
     */
    @Transaction
    suspend fun replaceAllDrivers(drivers: List<DriverEntity>) {
        deleteAllDrivers()
        insertDrivers(drivers)
    }

    @Query("SELECT * FROM drivers WHERE sessionKey = :sessionKey ORDER BY driverNumber ASC")
    fun getDriversForSession(sessionKey: Int): Flow<List<DriverEntity>>

    @Query("SELECT * FROM drivers WHERE driverNumber = :driverNumber LIMIT 1")
    fun getDriverByNumber(driverNumber: Int): Flow<DriverEntity?>

    @Query("SELECT * FROM drivers WHERE id = :id")
    fun getDriverById(id: String): Flow<DriverEntity?>

    @Query("SELECT lastUpdated FROM drivers WHERE sessionKey = :sessionKey ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLastUpdatedForSession(sessionKey: Int): Long?

    @Query("DELETE FROM drivers WHERE sessionKey = :sessionKey")
    suspend fun deleteDriversForSession(sessionKey: Int)

    @Query("DELETE FROM drivers")
    suspend fun deleteAllDrivers()

    @Query("SELECT * FROM drivers ORDER BY championshipPoints DESC, driverNumber ASC")
    fun getAllDrivers(): Flow<List<DriverEntity>>

    @Query("SELECT lastUpdated FROM drivers ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLatestUpdateTime(): Long?
}
