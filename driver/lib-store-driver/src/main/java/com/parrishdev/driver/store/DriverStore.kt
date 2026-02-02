package com.parrishdev.driver.store

import com.parrishdev.driver.model.Driver
import kotlinx.coroutines.flow.Flow

/**
 * Store interface for driver data.
 *
 * Follows the pattern: UI observes reactive Flows, Store handles
 * caching logic and network refresh.
 *
 * Data flow: Network -> Database -> UI (single source of truth)
 */
interface DriverStore {

    /**
     * Stream all drivers for a given session.
     * Returns data from local database, automatically updated when refresh completes.
     *
     * @param sessionKey The session key (e.g., 9158)
     * @return Flow of drivers, sorted by driver number
     */
    fun streamDrivers(sessionKey: Int): Flow<List<Driver>>

    /**
     * Stream a specific driver by their number.
     *
     * @param driverNumber The driver's racing number
     * @return Flow of driver, or null if not found
     */
    fun streamDriver(driverNumber: Int): Flow<Driver?>

    /**
     * Stream all drivers regardless of session.
     * Useful for offline viewing of cached data.
     *
     * @return Flow of all cached drivers
     */
    fun streamAllDrivers(): Flow<List<Driver>>

    /**
     * Refresh driver data from the network for a specific session.
     * Data is saved to database; observers will automatically receive updates.
     *
     * @param sessionKey The session key
     * @param force If true, ignores staleness check and always fetches
     */
    suspend fun refreshDrivers(sessionKey: Int, force: Boolean = false)

    /**
     * Refresh driver data from the network using the latest session.
     * Fetches drivers from the most recent F1 session (current/active season).
     * Data is saved to database; observers will automatically receive updates.
     *
     * @param force If true, ignores staleness check and always fetches
     */
    suspend fun refreshLatestDrivers(force: Boolean = false)
}
