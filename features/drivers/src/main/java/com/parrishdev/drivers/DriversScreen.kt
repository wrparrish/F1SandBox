package com.parrishdev.drivers

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation


@Composable
fun DriversScreen() {
    Column {
        Text("Drivers Screen")
    }
}

fun NavGraphBuilder.driversGraph() {
    navigation(startDestination = Routes.DRIVERS_SCREEN, route = Routes.DRIVERS_GRAPH) {
        composable(Routes.DRIVERS_SCREEN) { DriversScreen() }
    }
}