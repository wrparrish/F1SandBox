package com.parrishdev.settings.feature

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.parrishdev.settings.contracts.SettingsGraph
import com.parrishdev.settings.contracts.SettingsScreen as SettingsScreenRoute

/**
 * Type-safe navigation graph for the Settings domain.
 * Uses Navigation Compose 2.8+ typed routes.
 */
fun NavGraphBuilder.settingsGraph() {
    navigation<SettingsGraph>(
        startDestination = SettingsScreenRoute
    ) {
        composable<SettingsScreenRoute> {
            SettingsScreen()
        }
    }
}
