package com.parrishdev.data.mappers

import com.parrishdev.model.DriverStanding as DomainDriverStanding
import com.parrishdev.network.responses.DriverStandingsResponse
import com.parrishdev.network.responses.DriverStanding as NetworkDriverStanding

fun DriverStandingsResponse.toDomainModelList(): List<DomainDriverStanding> {
    return this.mrData.standingsTable?.standingsLists?.firstOrNull()?.driverStandings?.map { networkStanding ->
        networkStanding.toDomainModel()
    } ?: emptyList()
}

fun NetworkDriverStanding.toDomainModel(): DomainDriverStanding {
    val firstConstructor = this.constructors.firstOrNull()?.name ?: "N/A"
    return DomainDriverStanding(
        driverId = this.driver.driverId,
        position = this.position,
        points = this.points,
        wins = this.wins,
        constructorName = firstConstructor,
        driverName = "${this.driver.givenName} ${this.driver.familyName}",
        driverCode = this.driver.code,
        driverNumber = this.driver.permanentNumber
    )
}
