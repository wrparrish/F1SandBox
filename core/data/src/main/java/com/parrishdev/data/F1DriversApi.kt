package com.parrishdev.data

import com.parrishdev.data.mappers.toDataModel
import com.parrishdev.data.mappers.toDomainModelList as toDriverStandingDomainModelList
import com.parrishdev.data.mappers.toDomainModelList as toDriverResultDomainModelList
import com.parrishdev.model.Driver
import com.parrishdev.model.DriverRaceResult
import com.parrishdev.model.DriverStanding
import com.parrishdev.network.ErgastEndpoint
import com.parrishdev.network.F1Endpoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface F1DriversApi {
    suspend fun fetchDrivers(year: String): Result<List<Driver>>
    suspend fun fetchDriverStandings(year: String): Result<List<DriverStanding>>
    suspend fun fetchDriverResults(driverId: String): Result<List<DriverRaceResult>>
}

class F1DriversApiImpl @Inject constructor(
    private val f1Endpoint: F1Endpoint,
    private val ergastEndpoint: ErgastEndpoint // Added ErgastEndpoint
) : F1DriversApi {
    override suspend fun fetchDrivers(year: String): Result<List<Driver>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                f1Endpoint.getDrivers(year = year)
                    .filter { !it.teamName.isNullOrEmpty() }
                    .map { it.toDataModel() }
            }
        }
    }

    override suspend fun fetchDriverStandings(year: String): Result<List<DriverStanding>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                ergastEndpoint.getDriverStandings(year = year)
                    .toDriverStandingDomainModelList()
            }
        }
    }

    override suspend fun fetchDriverResults(driverId: String): Result<List<DriverRaceResult>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                // Using default limit and offset as defined in ErgastEndpoint
                ergastEndpoint.getDriverResults(driverId = driverId)
                    .toDriverResultDomainModelList()
            }
        }
    }
}


