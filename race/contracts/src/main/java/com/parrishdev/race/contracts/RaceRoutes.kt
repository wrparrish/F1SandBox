package com.parrishdev.race.contracts

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes for the Race domain.
 *
 * Uses Kotlin Serialization with Navigation Compose 2.8+ for
 * compile-time safe navigation arguments.
 */

/**
 * Navigation graph for the Race domain.
 * Contains Home and Results screens.
 */
@Serializable
object RaceGraph

/**
 * Home screen showing list of races for the season.
 */
@Serializable
object RaceHomeScreen

/**
 * Results screen showing race results for a specific race.
 *
 * @param season The F1 season year
 * @param round The round number within the season
 */
@Serializable
data class RaceResultsScreen(
    val season: Int,
    val round: Int
)
