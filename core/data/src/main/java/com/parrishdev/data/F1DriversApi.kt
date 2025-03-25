package com.parrishdev.data

import com.parrishdev.model.Driver
import com.parrishdev.network.F1Endpoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface F1DriversApi {
    suspend fun fetchDrivers(): Result<List<Driver>>

}


class F1DriversApiImpl @Inject constructor(private val f1Endpoint: F1Endpoint) : F1DriversApi {
    override suspend fun fetchDrivers(): Result<List<Driver>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                f1Endpoint.getDrivers()
                    .filter { !it.teamName.isNullOrEmpty() }
            }
        }
    }
}


