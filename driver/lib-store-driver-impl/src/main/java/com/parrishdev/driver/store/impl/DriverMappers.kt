package com.parrishdev.driver.store.impl

import com.parrishdev.driver.api.model.DriverDto
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
