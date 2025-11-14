package com.parrishdev.data.mappers

import com.parrishdev.model.DriverRaceResult as DomainDriverRaceResult
import com.parrishdev.network.responses.RaceTableResponse
import com.parrishdev.network.responses.RacesItem
import com.parrishdev.network.responses.ResultsItem

fun RaceTableResponse.toDomainModelList(): List<DomainDriverRaceResult> {
    return this.mrData.raceTable.races?.flatMap { raceItem ->
        raceItem.results?.mapNotNull { resultItem ->
            resultItem.toDomainModel(raceItem)
        } ?: emptyList()
    } ?: emptyList()
}

fun ResultsItem.toDomainModel(raceInfo: RacesItem): DomainDriverRaceResult? {
    // A race result must have a driver
    if (this.driver == null) return null

    return DomainDriverRaceResult(
        season = raceInfo.season ?: "N/A",
        round = raceInfo.round ?: "N/A",
        raceName = raceInfo.raceName ?: "N/A",
        date = raceInfo.date ?: "N/A",
        constructorName = this.constructor?.name ?: "N/A",
        grid = this.grid ?: "N/A",
        position = this.position ?: "N/A",
        points = this.points ?: "0",
        status = this.status ?: "N/A",
        driverId = this.driver.driverId, // Assuming driverId is non-null for a result
        circuitName = raceInfo.circuit?.circuitName ?: "N/A"
    )
}
