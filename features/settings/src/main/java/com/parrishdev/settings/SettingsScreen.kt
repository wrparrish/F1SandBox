package com.parrishdev.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation


@Composable
fun SettingsScreen() {
    Column {
       Text("Settings Screen")
    }
}

fun NavGraphBuilder.settingsGraph() {
    navigation(startDestination = Routes.SETTINGS_SCREEN, route = Routes.SETTINGS_GRAPH) {
        composable(Routes.SETTINGS_SCREEN) { SettingsScreen() }
    }
}