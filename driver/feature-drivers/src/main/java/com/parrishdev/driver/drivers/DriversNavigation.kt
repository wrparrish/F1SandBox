package com.parrishdev.driver.drivers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.parrishdev.driver.contracts.DriverDetailsRoute
import com.parrishdev.driver.contracts.DriverGraph
import com.parrishdev.driver.contracts.DriversListRoute

/**
 * Type-safe navigation graph for the Driver domain.
 * Uses Navigation Compose 2.8+ typed routes.
 */
fun NavGraphBuilder.driverGraph(
    rootNavController: NavHostController,
    selectedSeason: Int
) {
    navigation<DriverGraph>(
        startDestination = DriversListRoute
    ) {
        composable<DriversListRoute> {
            DriversScreen(
                selectedSeason = selectedSeason,
                onNavigateToDetails = { driverNumber, season ->
                    rootNavController.navigate(DriverDetailsRoute(driverNumber = driverNumber, season = season))
                }
            )
        }

        composable<DriverDetailsRoute> {
            DriverDetailScreen(
                onBackClick = {
                    rootNavController.popBackStack()
                }
            )
        }
    }
}
