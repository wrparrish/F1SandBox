package com.parrishdev.network



import com.parrishdev.network.responses.Driver
import com.parrishdev.network.responses.DriverStandingsResponse
import com.parrishdev.network.responses.Meeting
import com.parrishdev.network.responses.RaceResultsResponse
import com.parrishdev.network.responses.RaceTableResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface F1Endpoint {
    // Fetches drivers for a specific year.
    // According to Ergast API documentation (http://ergast.com/mrd/),
    // data can be retrieved by specifying the season (year) in the path.
    // e.g., http://ergast.com/api/f1/<season>/drivers.json
    @GET("{year}/drivers.json")
    suspend fun getDrivers(@Path("year") year: String): List<Driver>

    @GET("meetings")
    suspend fun getMeetings(@Query("year") year: String): List<Meeting>
}

interface  ErgastEndpoint {
    @GET("{year}/results.json")
    suspend fun getResults(@Path("year") year: String): RaceResultsResponse

    @GET("{year}/driverStandings.json")
    suspend fun getDriverStandings(@Path("year") year: String): DriverStandingsResponse

    @GET("drivers/{driverId}/results.json")
    suspend fun getDriverResults(@Path("driverId") driverId: String, @Query("limit") limit: Int = 1000, @Query("offset") offset: Int = 0): RaceTableResponse
}
