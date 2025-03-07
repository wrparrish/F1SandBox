package com.parrishdev.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation


@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    Column {
       Text("Settings Screen")
    }
}

fun NavGraphBuilder.settingsGraph() {
    navigation(startDestination = Routes.Settings.SCREEN, route = Routes.Settings.GRAPH) {
        composable(Routes.Settings.SCREEN) { SettingsScreen() }
    }
}