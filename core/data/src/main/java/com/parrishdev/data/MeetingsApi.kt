package com.parrishdev.data

import com.parrishdev.model.Meeting
import com.parrishdev.model.RaceResultsResponse
import com.parrishdev.network.ErgastEndpoint
import com.parrishdev.network.F1Endpoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

interface MeetingsApi {
    suspend fun fetchRaceResults(): Result<RaceResultsResponse>
}

class MeetingsApiImpl @Inject constructor(
    @Named("Ergast") private val ergastEndpoint: ErgastEndpoint
) : MeetingsApi {
    override suspend fun fetchRaceResults(): Result<RaceResultsResponse> {
        return withContext(Dispatchers.IO) {
            runCatching {
                ergastEndpoint.getResults()
            }
        }
    }
}