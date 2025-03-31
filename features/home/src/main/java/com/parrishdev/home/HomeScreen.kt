package com.parrishdev.home

import Routes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.parrishdev.model.Circuit
import com.parrishdev.model.Location
import com.parrishdev.model.RaceWithResultData
import com.parrishdev.model.TestFactory
import com.parrishdev.navigation.SharedViewModel
import com.parrishdev.ui.common.Error
import com.parrishdev.ui.common.Loading

@Composable
fun HomeScreen(
    rootNavController: NavController
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state = viewModel.viewState.collectAsState().value
    val events = state.events

    LaunchedEffect(key1 = true) {
        viewModel.viewEffects.collect {
            when (it) {
                is HomeEffect.GoToMeeting -> {
                    rootNavController.navigate(Routes.MeetingDetails.createRoute(it.meetingId))
                }
            }
        }
    }

    when {
        state.isLoading -> {
            Loading()
        }

        state.errorMessage != null -> {
            Error(
                errorMessage = state.errorMessage
            )
        }

        else -> {
            RaceResultList(events, state.results)
        }
    }
}

@Composable
fun RaceList(events: (HomeEvent) -> Unit, meetings: List<RaceWithResultData>) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = listState
    ) {
        items(meetings) { meeting ->
            RaceCard(events, meeting)
        }
    }
}

@Composable
fun RaceResultList(events: (HomeEvent) -> Unit, races: List<RaceWithResultData>) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = listState
    ) {
        items(races) { race ->
            RaceResultCard(events, race)
        }
    }
}

@Composable
fun RaceResultCard(events: (HomeEvent) -> Unit, race: RaceWithResultData) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(16.dp)
            .clickable {
                events(HomeEvent.MeetingSelected(race.round.toInt()))

            }) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = race.raceName,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun RaceCard(
    events: (HomeEvent) -> Unit,
    race: RaceWithResultData,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .padding(16.dp)
            .clickable {
                events(HomeEvent.MeetingSelected(race.round.toInt()))
            },
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = race.raceName,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)
            )
        }
    }
}


fun NavGraphBuilder.homeGraph(
    rootNavController: NavController,
    sharedViewModel: SharedViewModel
) {
    navigation(startDestination = Routes.Home.SCREEN, route = Routes.Home.GRAPH) {
        composable(Routes.Home.SCREEN) {
            sharedViewModel.updateTitle(LocalContext.current.getString(R.string.home_screen))
            HomeScreen(rootNavController)
        }
    }

    composable(
        route = Routes.MeetingDetails.SCREEN,
        arguments = listOf(navArgument(Routes.MeetingDetails.ARG_MEETING_ID) {
            type = NavType.StringType
        })
    ) {
        Text("Meeting Details Screen for ${it.arguments?.getString(Routes.MeetingDetails.ARG_MEETING_ID)}")
    }

}

@Composable
@Preview
fun MeetingCardPreview() {
    val race = RaceWithResultData(
        date = "2023-03-10",
        round = "1",
        results = null,
        season = "2023",
        circuit = Circuit(
            circuitId = "test",
            url = "test",
            circuitName = "test",
            Location(
                lat = "test",
                long = "test",
                locality = "test",

                )
        )
    )
    RaceCard({}, race)

}

@Composable
@Preview
fun RaceResultCardPreview() {
    RaceResultCard({}, TestFactory.createResultsItem())
}
