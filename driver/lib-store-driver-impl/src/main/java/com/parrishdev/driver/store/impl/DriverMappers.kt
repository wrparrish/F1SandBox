package com.parrishdev.driver.store.impl

import com.parrishdev.driver.api.model.DriverDto
import com.parrishdev.driver.api.model.DriverStandingDto
import com.parrishdev.driver.api.model.StandingsDriverDto
import com.parrishdev.driver.db.DriverEntity
import com.parrishdev.driver.model.Driver

// ==================== API DTO -> Entity Mappers ====================

/**
 * Convert API DTO to database entity.
 * Returns null if required fields are missing.
 */
fun DriverDto.toEntity(): DriverEntity? {
    val number = driverNumber ?: return null
    val session = sessionKey ?: return null
    val team = teamName ?: return null

    return DriverEntity(
        id = DriverEntity.createId(session, number),
        driverNumber = number,
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        fullName = fullName ?: "",
        nameAcronym = nameAcronym ?: "",
        countryCode = countryCode ?: "",
        teamName = team,
        teamColour = teamColour ?: "",
        headshotUrl = headshotUrl,
        broadcastName = broadcastName ?: "",
        sessionKey = session
    )
}

/**
 * Convert API DTO to database entity.
 * Returns null if required fields are missing.
 * This is used for the fallback mapping when the OpenF1 API is down.
 */
fun DriverStandingDto.toEntity(season: Int): DriverEntity? {
    val standingDriver = driver
    val number = standingDriver.permanentNumber?.toIntOrNull() ?: return null

    return DriverEntity(
        id = DriverEntity.createId(season, number),
        driverNumber = number,
        firstName = standingDriver.givenName,
        lastName = standingDriver.familyName,
        fullName = standingDriver.givenName + " " + standingDriver.familyName,
        nameAcronym = standingDriver.code?.uppercase() ?: "",
        countryCode = standingDriver.nationality,
        teamName = constructors?.firstOrNull()?.name.orEmpty(),
        teamColour =  "",
        headshotUrl = null,
        broadcastName = "",
        sessionKey = -1,
    )
}


// ==================== Entity -> Domain Model Mappers ====================

/**
 * Convert database entity to domain model.
 */
fun DriverEntity.toDomain(): Driver {
    return Driver(
        driverNumber = driverNumber,
        firstName = firstName,
        lastName = lastName,
        fullName = fullName,
        nameAcronym = nameAcronym,
        countryCode = countryCode,
        teamName = teamName,
        teamColour = teamColour,
        headshotUrl = headshotUrl,
        broadcastName = broadcastName,
        sessionKey = sessionKey,
        championshipPosition = championshipPosition,
        championshipPoints = championshipPoints,
        wins = wins
    )
}
