package com.parrishdev.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation

@Composable
fun HomeScreen() {
    Column {
        Text("Home Screen")
    }
}


fun NavGraphBuilder.homeGraph() {
    navigation(startDestination = Routes.HOME_SCREEN, route = Routes.HOME_GRAPH) {
        composable(Routes.HOME_SCREEN) { HomeScreen() }
    }
}