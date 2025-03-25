package com.parrishdev.home

import Routes
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.parrishdev.model.Meeting
import com.parrishdev.navigation.SharedViewModel
import com.parrishdev.ui.common.Error
import com.parrishdev.ui.common.Loading

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.viewState.collectAsState().value
    val meetings = state.meetings

    when {
        state.isLoading -> {
            Loading()
        }

        state.errorMessage != null -> {
            Error(
                errorMessage = state.errorMessage
            )
        }
    }

    MeetingList(meetings)

}

@Composable
fun MeetingList(meetings: List<Meeting>) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = listState
    ) {
        items(meetings) { meeting ->
            MeetingCard(meeting)
        }
    }
}

@Composable
fun MeetingCard(meeting: Meeting, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .padding(16.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = meeting.meetingName,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
            )
        }
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