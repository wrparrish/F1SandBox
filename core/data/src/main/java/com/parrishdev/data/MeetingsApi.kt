package com.parrishdev.data

import com.parrishdev.data.mappers.toDataModel
import com.parrishdev.model.RaceWithResultData
import com.parrishdev.network.ErgastEndpoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

interface MeetingsApi {
    suspend fun fetchRaceResults(): Result<List<RaceWithResultData>>
}

class MeetingsApiImpl @Inject constructor(
    @Named("Ergast") private val ergastEndpoint: ErgastEndpoint
) : MeetingsApi {
    override suspend fun fetchRaceResults(): Result<List<RaceWithResultData>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                ergastEndpoint.getResults()
                    .data
                    .raceTable
                    .races?.map { it.toDataModel() }.orEmpty()
            }
        }
    }
}