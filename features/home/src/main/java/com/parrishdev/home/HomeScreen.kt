package com.parrishdev.home

import Routes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.parrishdev.navigation.SharedViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.fillMaxSize()) {

    }
}


fun NavGraphBuilder.homeGraph(sharedViewModel: SharedViewModel) {
    navigation(startDestination = Routes.HOME_SCREEN, route = Routes.HOME_GRAPH) {
        composable(Routes.HOME_SCREEN) {
            sharedViewModel.updateTitle(LocalContext.current.getString(R.string.home_screen))
            HomeScreen()
        }
    }
}