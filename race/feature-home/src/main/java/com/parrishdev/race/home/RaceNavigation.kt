package com.parrishdev.race.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.parrishdev.race.contracts.RaceGraph
import com.parrishdev.race.contracts.RaceHomeScreen
import com.parrishdev.race.contracts.RaceResultsScreen
import com.parrishdev.race.results.ResultsScreen

/**
 * Type-safe navigation graph for the Race domain.
 * Uses Navigation Compose 2.8+ typed routes.
 */
fun NavGraphBuilder.raceGraph(
    rootNavController: NavHostController
) {
    navigation<RaceGraph>(
        startDestination = RaceHomeScreen
    ) {
        composable<RaceHomeScreen> {
            HomeScreen(
                onNavigateToResults = { season, round ->
                    rootNavController.navigate(RaceResultsScreen(season, round))
                }
            )
        }

        composable<RaceResultsScreen> { backStackEntry ->
            // Type-safe argument extraction
            val args = backStackEntry.toRoute<RaceResultsScreen>()
            ResultsScreen()
        }
    }
}
