package com.parrishdev.driver.drivers

import com.parrishdev.driver.model.Driver

/**
 * UI-ready state for DriversScreen.
 *
 * Contains only data needed by the UI to render.
 * NO derived properties - only constructor properties.
 */
data class DriversViewState(
    val isLoading: Boolean,
    val isRefreshing: Boolean,
    val drivers: List<Driver>,
    val errorMessage: String?,
    val showEmptyState: Boolean
)
