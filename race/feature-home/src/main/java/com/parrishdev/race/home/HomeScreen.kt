package com.parrishdev.race.home

import com.parrishdev.ui.common.singleClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.parrishdev.race.fixtures.createRaceList
import com.parrishdev.race.model.Race
import com.parrishdev.ui.F1Red
import com.parrishdev.ui.F1SandboxTheme
import com.parrishdev.ui.GhostGrey
import com.parrishdev.ui.common.Error
import com.parrishdev.ui.common.Loading

@Composable
fun HomeScreen(
    onNavigateToResults: (season: Int, round: Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewState by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(lifecycleOwner) {
        viewModel.onStartObserving(lifecycleOwner)
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is HomeEvent.NavigateToRaceResults -> {
                    onNavigateToResults(event.season, event.round)
                }
                is HomeEvent.ShowError -> {}
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
        viewState.isLoading -> Loading()

        viewState.errorMessage != null && viewState.races.isEmpty() -> {
            Error(errorMessage = viewState.errorMessage, onRetry = onRetry)
        }

        viewState.showEmptyState -> EmptyState()

        else -> {
            PullToRefreshBox(
                isRefreshing = viewState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                RaceList(races = viewState.races, onRaceSelected = onRaceSelected)
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
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = races, key = { it.id }) { race ->
            RaceCard(race = race, onClick = { onRaceSelected(race) })
        }
    }
}

@Composable
private fun RaceCard(
    race: Race,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .singleClickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Red accent strip
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .background(F1Red)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = "ROUND ${race.round}",
                    style = MaterialTheme.typography.labelMedium,
                    color = F1Red,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = race.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = race.circuit.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${race.circuit.location.locality}, ${race.circuit.location.country}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GhostGrey,
                    )
                    Text(
                        text = formatRaceDate(race.date),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private fun formatRaceDate(dateString: String): String {
    return try {
        val parts = dateString.split("-")
        val month = when (parts[1]) {
            "01" -> "JAN"; "02" -> "FEB"; "03" -> "MAR"; "04" -> "APR"
            "05" -> "MAY"; "06" -> "JUN"; "07" -> "JUL"; "08" -> "AUG"
            "09" -> "SEP"; "10" -> "OCT"; "11" -> "NOV"; "12" -> "DEC"
            else -> parts[1]
        }
        "${parts[2].trimStart('0')} $month"
    } catch (_: Exception) {
        dateString
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "NO RACES FOUND",
                style = MaterialTheme.typography.labelLarge,
                color = GhostGrey,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pull down to refresh",
                style = MaterialTheme.typography.bodyMedium,
                color = GhostGrey,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0F)
@Composable
private fun RaceListPreview() {
    F1SandboxTheme(darkTheme = true) {
        RaceList(races = createRaceList(), onRaceSelected = {})
    }
}
