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
import com.parrishdev.driver.contracts.DriverDetailsScreen
import com.parrishdev.driver.contracts.DriverGraph
import com.parrishdev.driver.contracts.DriversListScreen

/**
 * Type-safe navigation graph for the Driver domain.
 * Uses Navigation Compose 2.8+ typed routes.
 */
fun NavGraphBuilder.driverGraph(
    rootNavController: NavHostController
) {
    navigation<DriverGraph>(
        startDestination = DriversListScreen
    ) {
        composable<DriversListScreen> {
            DriversScreen(
                onNavigateToDetails = { driverNumber ->
                    rootNavController.navigate(DriverDetailsScreen(driverNumber))
                }
            )
        }

        composable<DriverDetailsScreen> { backStackEntry ->
            // Type-safe argument extraction
            val args = backStackEntry.toRoute<DriverDetailsScreen>()
            // Placeholder for driver details screen
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Driver Details Screen for #${args.driverNumber}")
            }
        }
    }
}
