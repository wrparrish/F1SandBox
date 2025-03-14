package com.parrishdev.drivers

import Routes
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.parrishdev.model.Driver
import com.parrishdev.navigation.SharedViewModel
import com.parrishdev.ui.common.DriverHeadshot


@Composable
fun DriversScreen(
    rootNavController: NavController,
    viewModel: DriversViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsState()

    when {
        state.value.isLoading -> {
            Loading()
        }

        state.value.errorMessage != null -> {
            Error(
                errorMessage = state.value.errorMessage
                    ?: stringResource(R.string.text_unknown_error)
            )
        }

        else -> {
            DriversList(viewModel, state.value.drivers)
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToDetails -> {
                    rootNavController.navigate(Routes.DriverDetails.createRoute(event.driverId))
                }

                NavigationEvent.NavigateBack -> rootNavController.popBackStack()
            }
        }
    }

}

@Composable
fun DriversList(viewModel: DriversViewModel, drivers: List<Driver>, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState
    ) {
        items(drivers) { driver ->
            DriverCard(
                driver, modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(16.dp)
                    .clickable {
                        viewModel.onDriverSelected(driver)
                    }
            )
        }
    }
}

@Composable
fun DriverCard(driver: Driver, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row {
            DriverHeadshot(
                driver, modifier = Modifier
                    .align(Alignment.Bottom)
                    .height(120.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = driver.fullName ?: "bad data, check logs")
                Text(text = driver.teamName ?: "bad data, check logs")
            }

            Box(modifier = Modifier.fillMaxWidth()) {

                Text(
                    text = driver.driverNumber.toString(),
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                )
            }
        }
    }
}


@Composable
fun Loading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun Error(errorMessage: String) {
    Text(errorMessage)
}

@Preview(showBackground = true)
@Composable
fun DriverEntryPreview() {
    val driver = Driver(
        driverNumber = 14,
        countryCode = "ES",
        fullName = "Fernando Alonso",
        teamName = "Aston Martin",
        headshotUrl = "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/F/FERALO01_Fernando_Alonso/feralo01.png.transform/1col/image.png"
    )
    DriverCard(
        driver, modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(16.dp)
    )


}


fun NavGraphBuilder.driversGraph(
    rootNavController: NavController,
    sharedViewModel: SharedViewModel
) {
    navigation(startDestination = Routes.Drivers.SCREEN, route = Routes.Drivers.GRAPH) {
        composable(route = Routes.Drivers.SCREEN) {
            sharedViewModel.updateTitle("Drivers")
            DriversScreen(
                rootNavController
            )
        }
        composable(
            route = Routes.DriverDetails.SCREEN,
            arguments = listOf(navArgument(Routes.DriverDetails.ARG_DRIVER_ID) {
                type = NavType.StringType
            })
        ) {
            Log.e(
                "DriverDetailsScreen",
                "DriverDetailsScreen: ${it.arguments?.getString(Routes.DriverDetails.ARG_DRIVER_ID)}"
            )
            Text("Driver Details Screen for ${it.arguments?.getString(Routes.DriverDetails.ARG_DRIVER_ID)}")
        }
    }
}