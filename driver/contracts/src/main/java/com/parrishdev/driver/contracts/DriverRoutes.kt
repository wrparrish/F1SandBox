package com.parrishdev.driver.contracts

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes for the Driver domain.
 *
 * Uses Kotlin Serialization with Navigation Compose 2.8+ for
 * compile-time safe navigation arguments.
 */

/**
 * Navigation graph for the Driver domain.
 * Contains Drivers list and Driver details screens.
 */
@Serializable
object DriverGraph

/**
 * Drivers list screen showing all F1 drivers.
 */
@Serializable
object DriversListScreen

/**
 * Driver details screen showing information about a specific driver.
 *
 * @param driverNumber The unique driver number
 */
@Serializable
data class DriverDetailsScreen(
    val driverNumber: Int
)
