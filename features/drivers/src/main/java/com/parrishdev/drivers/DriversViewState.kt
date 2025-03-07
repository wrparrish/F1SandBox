package com.parrishdev.drivers

import com.parrishdev.model.Driver

data class DriversViewState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val drivers: List<Driver> = emptyList(),
)

sealed class NavigationEvent {
    data class NavigateToDetails(val driverId: String) : NavigationEvent()
    data object NavigateBack : NavigationEvent()
}
