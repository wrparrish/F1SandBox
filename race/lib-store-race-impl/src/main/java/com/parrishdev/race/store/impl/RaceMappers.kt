package com.parrishdev.race.store.impl

import com.parrishdev.race.api.model.CircuitDto
import com.parrishdev.race.api.model.ConstructorDto
import com.parrishdev.race.api.model.DriverDto
import com.parrishdev.race.api.model.FastestLapDto
import com.parrishdev.race.api.model.LocationDto
import com.parrishdev.race.api.model.RaceDto
import com.parrishdev.race.api.model.ResultDto
import com.parrishdev.race.db.RaceEntity
import com.parrishdev.race.db.RaceResultEntity
import com.parrishdev.race.model.AverageSpeed
import com.parrishdev.race.model.Circuit
import com.parrishdev.race.model.Constructor
import com.parrishdev.race.model.FastestLap
import com.parrishdev.race.model.Location
import com.parrishdev.race.model.Race
import com.parrishdev.race.model.RaceDriver
import com.parrishdev.race.model.RaceResult
import com.parrishdev.race.model.RaceWithResults

// ==================== API DTO -> Entity Mappers ====================

/**
 * Convert API DTO to database entity.
 * Handles nullable circuit and time fields defensively.
 */
fun RaceDto.toEntity(): RaceEntity {
    val seasonInt = season.toIntOrNull() ?: 0
    val roundInt = round.toIntOrNull() ?: 0
    return RaceEntity(
        id = RaceEntity.createId(seasonInt, roundInt),
        season = seasonInt,
        round = roundInt,
        name = raceName,
        date = date,
        time = time ?: "",
        circuitId = circuit?.circuitId ?: "",
        circuitName = circuit?.circuitName ?: "Unknown Circuit",
        circuitUrl = circuit?.url ?: "",
        locality = circuit?.location?.locality ?: "",
        country = circuit?.location?.country ?: "",
        latitude = circuit?.location?.lat ?: "",
        longitude = circuit?.location?.long ?: "",
        url = url
    )
}

/**
 * Convert API result DTO to database entity.
 */
fun ResultDto.toEntity(raceId: String): RaceResultEntity {
    return RaceResultEntity(
        id = RaceResultEntity.createId(raceId, driver.driverId),
        raceId = raceId,
        position = position.toIntOrNull() ?: 0,
        positionText = positionText,
        points = points.toFloatOrNull() ?: 0f,
        driverId = driver.driverId,
        driverPermanentNumber = driver.permanentNumber,
        driverCode = driver.code,
        driverGivenName = driver.givenName,
        driverFamilyName = driver.familyName,
        driverNationality = driver.nationality,
        driverUrl = driver.url,
        constructorId = constructor.constructorId,
        constructorName = constructor.name,
        constructorNationality = constructor.nationality,
        constructorUrl = constructor.url,
        grid = grid.toIntOrNull() ?: 0,
        laps = laps.toIntOrNull() ?: 0,
        status = status,
        fastestLapRank = fastestLap?.rank?.toIntOrNull(),
        fastestLapLap = fastestLap?.lap?.toIntOrNull(),
        fastestLapTime = fastestLap?.time?.time,
        fastestLapSpeedUnits = fastestLap?.averageSpeed?.units,
        fastestLapSpeed = fastestLap?.averageSpeed?.speed
    )
}

// ==================== Entity -> Domain Model Mappers ====================

/**
 * Convert database entity to domain model.
 */
fun RaceEntity.toDomain(): Race {
    return Race(
        id = id,
        season = season,
        round = round,
        name = name,
        date = date,
        time = time,
        circuit = Circuit(
            id = circuitId,
            name = circuitName,
            location = Location(
                locality = locality,
                country = country,
                latitude = latitude,
                longitude = longitude
            ),
            url = circuitUrl
        ),
        url = url
    )
}

/**
 * Convert database entity to domain model.
 */
fun RaceResultEntity.toDomain(): RaceResult {
    return RaceResult(
        position = position,
        positionText = positionText,
        points = points,
        driver = RaceDriver(
            id = driverId,
            permanentNumber = driverPermanentNumber,
            code = driverCode,
            givenName = driverGivenName,
            familyName = driverFamilyName,
            nationality = driverNationality,
            url = driverUrl
        ),
        constructor = Constructor(
            id = constructorId,
            name = constructorName,
            nationality = constructorNationality,
            url = constructorUrl
        ),
        grid = grid,
        laps = laps,
        status = status,
        fastestLap = run {
            // Capture nullable properties in local vals for smart casting
            val rank = fastestLapRank
            val lap = fastestLapLap
            val time = fastestLapTime
            val speedUnits = fastestLapSpeedUnits
            val speed = fastestLapSpeed

            if (rank != null && lap != null && time != null) {
                FastestLap(
                    rank = rank,
                    lap = lap,
                    time = time,
                    averageSpeed = if (speedUnits != null && speed != null) {
                        AverageSpeed(units = speedUnits, speed = speed)
                    } else null
                )
            } else null
        }
    )
}

/**
 * Convert race entity and result entities to combined domain model.
 */
fun Pair<RaceEntity, List<RaceResultEntity>>.toDomain(): RaceWithResults {
    return RaceWithResults(
        race = first.toDomain(),
        results = second.map { it.toDomain() }
    )
}
