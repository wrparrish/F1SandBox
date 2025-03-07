package com.parrishdev.data

import com.parrishdev.model.Driver
import com.parrishdev.network.F1Endpoint
import javax.inject.Inject

interface F1DriversApi {
    suspend fun fetchDrivers(): Result<List<Driver>>
}


class F1DriversApiImpl @Inject constructor(private val f1Endpoint: F1Endpoint) : F1DriversApi {
    override suspend fun fetchDrivers(): Result<List<Driver>> {
           return runCatching {
                   f1Endpoint.getDrivers()
                       .filter { !it.teamName.isNullOrEmpty() }
                       .distinctBy {
                           it.fullName
                       }
               }
    }
}


