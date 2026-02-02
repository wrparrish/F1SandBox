package com.parrishdev.settings.contracts

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes for the Settings domain.
 *
 * Uses Kotlin Serialization with Navigation Compose 2.8+ for
 * compile-time safe navigation.
 */

/**
 * Navigation graph for the Settings domain.
 */
@Serializable
object SettingsGraph

/**
 * Settings screen showing app configuration options.
 */
@Serializable
object SettingsScreen
