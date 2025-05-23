package com.parrishdev.drivers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parrishdev.data.F1DriversApi
import com.parrishdev.model.Driver // Ensure this is the updated model
import com.parrishdev.model.DriverStanding
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DriversViewModel @Inject constructor(private val f1DriversApi: F1DriversApi) : ViewModel() {
    private val _uiState = MutableStateFlow(DriversViewState(
        events =  {
            when (it) {
                is DriversEvent.DriverSelected -> onDriverSelected(it.driverId) // driverId here is likely nameAcronym or similar
            }
        }
    ))
    val uiState = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        fetchDriversAndStandings()
    }

    private fun fetchDriversAndStandings() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")
        viewModelScope.launch {
            try {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()

                val standingsResult = f1DriversApi.fetchDriverStandings(currentYear)
                val driversResult = f1DriversApi.fetchDrivers(currentYear)

                if (standingsResult.isSuccess && driversResult.isSuccess) {
                    val standings = standingsResult.getOrThrow()
                    val drivers = driversResult.getOrThrow().toMutableList()

                    // Create a map of driverCode to DriverStanding for efficient lookup
                    val standingsMap = standings.associateBy { it.driverCode }

                    // Merge standings data into drivers list
                    drivers.forEach { driver ->
                        // Assuming nameAcronym from Driver model is the equivalent of driverCode in DriverStanding
                        standingsMap[driver.nameAcronym]?.let { standing ->
                            driver.position = standing.position
                            driver.points = standing.points
                        }
                    }

                    // Sort drivers:
                    // 1. By position (numeric, ascending). Null/non-numeric positions go to the end.
                    // 2. By points (numeric, descending).
                    drivers.sortWith(compareBy<Driver> {
                        it.position?.toIntOrNull() ?: Int.MAX_VALUE
                    }.thenByDescending {
                        it.points.toIntOrNull() ?: 0
                    })

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        drivers = drivers,
                        errorMessage = ""
                    )
                } else {
                    val standingsError = if (standingsResult.isFailure) "Failed to load standings: ${standingsResult.exceptionOrNull()?.message}. " else ""
                    val driversError = if (driversResult.isFailure) "Failed to load drivers: ${driversResult.exceptionOrNull()?.message}." else ""
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = (standingsError + driversError).trim()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "An unexpected error occurred: ${e.message}"
                )
            }
        }
    }

    // Assuming driverId passed here is the 'nameAcronym' or a similar unique ID from the Driver model
    fun onDriverSelected(driverId: String) {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToDetails(driverId))
        }
    }
}