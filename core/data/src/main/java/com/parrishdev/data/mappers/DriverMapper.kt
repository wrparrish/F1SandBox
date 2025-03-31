package com.parrishdev.data.mappers

import com.parrishdev.model.Driver
import com.parrishdev.network.responses.Driver as NetworkDriver


fun NetworkDriver.toDataModel(): Driver {
    return Driver(
        driverNumber = driverNumber,
        countryCode = countryCode,
        fullName = fullName,
        meetingKey = meetingKey,
        headshotUrl = headshotUrl,
        nameAcronym = nameAcronym,
        sessionKey = sessionKey,
        lastName = lastName,
        teamColour = teamColour,
        broadcastName = broadcastName,
        firstName = firstName,
        teamName = teamName
    )
}