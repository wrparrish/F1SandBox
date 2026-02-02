package com.parrishdev.driver.drivers

import com.parrishdev.driver.model.Driver

/**
 * Internal data state for [DriversViewModel].
 *
 * Contains raw data from stores and derived properties for business logic.
 * Nullable properties indicate loading state (null = loading).
 */
data class DriversDataState(
    val drivers: List<Driver>? = null,
    val isRefreshing: Boolean = false,
    val error: String? = null
) {
    /**
     * True if initial data is still loading (drivers is null and no error).
     */
    val isLoading: Boolean
        get() = drivers == null && error == null

    /**
     * True if there's data to display.
     */
    val hasData: Boolean
        get() = !drivers.isNullOrEmpty()
}
