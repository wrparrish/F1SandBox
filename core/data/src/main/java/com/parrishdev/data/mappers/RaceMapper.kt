package com.parrishdev.data.mappers

import com.parrishdev.model.Circuit
import com.parrishdev.model.Constructor
import com.parrishdev.model.RaceWithResultData
import com.parrishdev.model.ResultsItem

import com.parrishdev.network.responses.Circuit as NetworkCircuit
import com.parrishdev.network.responses.Constructor as NetworkConstructor
import com.parrishdev.network.responses.Location as NetworkLocation
import com.parrishdev.network.responses.RacesItem as NetworkRace
import com.parrishdev.network.responses.ResultsItem as NetworkResultItem


fun NetworkRace.toDataModel(): RaceWithResultData {
    return RaceWithResultData(
        date = date,
        results = results?.toDataModel(),
        round = round,
        season = season,
        raceName = raceName,
        circuit = circuit.toDataModel(),
        time = time,

        )
}

fun NetworkLocation.toDataModel(): com.parrishdev.model.Location {
    return com.parrishdev.model.Location(
        country = country,
        locality = locality,
        lat = lat,
        long = long,
    )
}

fun NetworkCircuit.toDataModel(): Circuit {
    return Circuit(
        location = location.toDataModel(),
    )
}

fun NetworkConstructor.toDataModel(): Constructor {
    return Constructor(
        nationality = nationality,
        name = name,
        constructorId = constructorId,
        url = url,
    )
}

fun List<NetworkResultItem>.toDataModel(): List<ResultsItem> {
    return map {
        it.toDataModel()
    }
}

fun NetworkResultItem.toDataModel(): ResultsItem {
    return ResultsItem(
        number = number,
        positionText = positionText,
        constructor = constructor.toDataModel(),
        grid = grid,
        driver = driver.toDataModel(),
    )

}