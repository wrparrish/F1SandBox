package com.parrishdev.driver.drivers

import com.parrishdev.driver.model.Driver

/**
 * UI-ready state for DriverDetailScreen.
 *
 * Contains only data needed by the UI to render.
 */
data class DriverDetailViewState(
    val isLoading: Boolean,
    val isRefreshing: Boolean,
    val driver: Driver?,
    val errorMessage: String?
)
