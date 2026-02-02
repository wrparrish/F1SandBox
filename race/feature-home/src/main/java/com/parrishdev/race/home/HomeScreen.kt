package com.parrishdev.race.home

import androidx.compose.foundation.clickable
import com.parrishdev.ui.common.singleClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.parrishdev.race.model.Circuit
import com.parrishdev.race.model.Location
import com.parrishdev.race.model.Race
import com.parrishdev.ui.common.Error
import com.parrishdev.ui.common.Loading

/**
 * Home screen displaying list of races for the current season.
 *
 * @param onNavigateToResults Called when user selects a race
 */
@Composable
fun HomeScreen(
    onNavigateToResults: (season: Int, round: Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewState by viewModel.stateFlow.collectAsStateWithLifecycle()

    // Set up lifecycle-aware store observations
    LaunchedEffect(lifecycleOwner) {
        viewModel.onStartObserving(lifecycleOwner)
    }

    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is HomeEvent.NavigateToRaceResults -> {
                    onNavigateToResults(event.season, event.round)
                }
                is HomeEvent.ShowError -> {
                    // Could show snackbar here
                }
            }
        }
    }

    HomeScreenContent(
        viewState = viewState,
        onRaceSelected = viewModel::onRaceSelected,
        onRefresh = viewModel::onRefresh,
        onRetry = viewModel::onRetry
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    viewState: HomeViewState,
    onRaceSelected: (Race) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit
) {
    when {
        viewState.isLoading -> {
            Loading()
        }

        viewState.errorMessage != null && viewState.races.isEmpty() -> {
            Error(
                errorMessage = viewState.errorMessage,
                onRetry = onRetry
            )
        }

        viewState.showEmptyState -> {
            EmptyState()
        }

        else -> {
            PullToRefreshBox(
                isRefreshing = viewState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                RaceList(
                    races = viewState.races,
                    onRaceSelected = onRaceSelected
                )
            }
        }
    }
}

@Composable
private fun RaceList(
    races: List<Race>,
    onRaceSelected: (Race) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = races,
            key = { it.id }
        ) { race ->
            RaceCard(
                race = race,
                onClick = { onRaceSelected(race) }
            )
        }
    }
}

@Composable
private fun RaceCard(
    race: Race,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .singleClickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Round ${race.round}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = race.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "${race.circuit.name} • ${race.circuit.location.country}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = race.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No races found",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RaceCardPreview() {
    val race = Race(
        id = "2025_1",
        season = 2025,
        round = 1,
        name = "Bahrain Grand Prix",
        date = "2025-03-02",
        time = "15:00:00Z",
        circuit = Circuit(
            id = "bahrain",
            name = "Bahrain International Circuit",
            location = Location(
                locality = "Sakhir",
                country = "Bahrain",
                latitude = "26.0325",
                longitude = "50.5106"
            ),
            url = ""
        ),
        url = ""
    )
    RaceCard(race = race, onClick = {})
}
