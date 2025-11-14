package com.parrishdev.drivers

import Routes
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Added
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.parrishdev.ui.R // Added
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
import com.parrishdev.ui.common.Error
import com.parrishdev.ui.common.Loading


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
                    //.height(160.dp) // Height can be dynamic
                    .padding(16.dp)
                    .clickable {
                        // Use nameAcronym for selection as it's used for matching with standings
                        viewModel.onDriverSelected(driver.nameAcronym ?: driver.driverNumber.toString())
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

            Column(modifier = Modifier.padding(16.dp).weight(1f)) { // Added weight for text column
                Text(text = driver.fullName ?: "Unknown Driver")
                Text(text = driver.teamName ?: "Unknown Team")
                driver.position?.let { // Display position if available
                    Text(text = "Pos: $it")
                }
                Text(text = "Pts: ${driver.points}") // Points defaults to "0"
            }

            Box(modifier = Modifier.padding(start = 8.dp)) { // Reduced padding for number box

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


@Preview(showBackground = true)
@Composable
fun DriverEntryPreview() {
    val driver = Driver(
        driverNumber = 14,
        countryCode = "ES",
        fullName = "Fernando Alonso",
        teamName = "Aston Martin",
        headshotUrl = "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/F/FERALO01_Fernando_Alonso/feralo01.png.transform/1col/image.png",
        points = "180",
        position = "4"
    )
    DriverCard(
        driver, modifier = Modifier
            .fillMaxWidth()
            //.height(160.dp) // Height can be dynamic
            .padding(16.dp)
    )
}


fun NavGraphBuilder.driversGraph(
    rootNavController: NavController,
    sharedViewModel: SharedViewModel
) {
    navigation(startDestination = Routes.Drivers.SCREEN, route = Routes.Drivers.GRAPH) {
        composable(route = Routes.Drivers.SCREEN) {
            val context = LocalContext.current
            sharedViewModel.updateTitle(context.getString(R.string.drivers_screen_title))
            DriversScreen(
                rootNavController = rootNavController
            )
        }
        composable(
            route = Routes.DriverDetails.SCREEN,
            arguments = listOf(navArgument(Routes.DriverDetails.ARG_DRIVER_ID) {
                type = NavType.StringType
                // Potentially add nullable = true if a driverId could ever be missing, though unlikely for this screen
            })
        ) {
            val context = LocalContext.current
            // The driverId is extracted in the DriverDetailsViewModel via SavedStateHandle
            // The driverId from the arguments can also be used to update the title dynamically if needed
            val driverIdArg = it.arguments?.getString(Routes.DriverDetails.ARG_DRIVER_ID)
            sharedViewModel.updateTitle(
                context.getString(
                    R.string.driver_details_screen_title_prefix,
                    driverIdArg?.uppercase() ?: "Driver" // Fallback if driverIdArg is null
                )
            )
            DriverDetailsScreen(
                navController = rootNavController
                // ViewModel will be hiltViewModel() by default in DriverDetailsScreen
            )
        }
    }
}