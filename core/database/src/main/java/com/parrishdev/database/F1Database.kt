package com.parrishdev.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.parrishdev.driver.db.DriverDao
import com.parrishdev.driver.db.DriverEntity
import com.parrishdev.race.db.RaceDao
import com.parrishdev.race.db.RaceEntity
import com.parrishdev.race.db.RaceResultEntity

/**
 * Main Room database for F1Sandbox app.
 *
 * This database aggregates all entities and DAOs from domain-specific modules:
 * - race/lib-db-race: RaceEntity, RaceResultEntity, RaceDao
 * - driver/lib-db-driver: DriverEntity, DriverDao
 *
 * The database is provided as a singleton via Hilt in DatabaseModule.
 */
@Database(
    entities = [
        RaceEntity::class,
        RaceResultEntity::class,
        DriverEntity::class,
    ],
    version = 4,  // Bumped to add championship standings columns to drivers
    exportSchema = false
)
abstract class F1Database : RoomDatabase() {

    abstract fun raceDao(): RaceDao

    abstract fun driverDao(): DriverDao
}
