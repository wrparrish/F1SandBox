package com.parrishdev.driver.drivers

import com.parrishdev.driver.model.Driver

/**
 * Internal data state for [DriverDetailViewModel].
 *
 * Contains raw driver data, refreshing status, and error messages.
 * Nullable driver property indicates loading state (null = loading).
 */
data class DriverDetailDataState(
    val driverNumber: Int,
    val season: Int,
    val driver: Driver? = null,
    val isRefreshing: Boolean = false,
    val error: String? = null
) {
    /**
     * True if initial data is still loading (driver is null and no error).
     */
    val isLoading: Boolean
        get() = driver == null && error == null

    /**
     * True if there's driver data to display.
     */
    val hasData: Boolean
        get() = driver != null
}
