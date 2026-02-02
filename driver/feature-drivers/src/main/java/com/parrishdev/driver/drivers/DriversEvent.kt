package com.parrishdev.driver.drivers

/**
 * One-time events emitted by [DriversViewModel].
 * These are consumed by the UI for navigation, toasts, etc.
 */
sealed interface DriversEvent {

    /**
     * Navigate to driver details screen.
     */
    data class NavigateToDriverDetails(
        val driverNumber: Int
    ) : DriversEvent

    /**
     * Show an error message (snackbar/toast).
     */
    data class ShowError(val message: String) : DriversEvent
}
