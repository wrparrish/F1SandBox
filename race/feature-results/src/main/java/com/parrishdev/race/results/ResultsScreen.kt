package com.parrishdev.race.results

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.parrishdev.race.fixtures.createRaceWithResults
import com.parrishdev.race.model.RaceResult
import com.parrishdev.ui.BronzeThird
import com.parrishdev.ui.F1Red
import com.parrishdev.ui.F1SandboxTheme
import com.parrishdev.ui.FastestLapPurple
import com.parrishdev.ui.GhostGrey
import com.parrishdev.ui.GoldFirst
import com.parrishdev.ui.GreenFlag
import com.parrishdev.ui.SilverSecond
import com.parrishdev.ui.common.Error
import com.parrishdev.ui.common.Loading

@Composable
fun ResultsScreen(
    viewModel: ResultsViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewState by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(lifecycleOwner) {
        viewModel.onStartObserving(lifecycleOwner)
    }

    ResultsScreenContent(
        viewState = viewState,
        onRefresh = viewModel::onRefresh,
        onRetry = viewModel::onRetry
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultsScreenContent(
    viewState: ResultsViewState,
    onRefresh: () -> Unit,
    onRetry: () -> Unit
) {
    when {
        viewState.isLoading -> Loading()

        viewState.errorMessage != null && viewState.results.isEmpty() -> {
            Error(errorMessage = viewState.errorMessage, onRetry = onRetry)
        }

        else -> {
            PullToRefreshBox(
                isRefreshing = viewState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                ResultsList(
                    raceName = viewState.raceName,
                    circuitName = viewState.circuitName,
                    date = viewState.date,
                    results = viewState.results
                )
            }
        }
    }
}

@Composable
private fun ResultsList(
    raceName: String,
    circuitName: String,
    date: String,
    results: List<RaceResult>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            RaceHeader(raceName = raceName, circuitName = circuitName, date = date)
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Podium section (top 3)
        val podium = results.filter { it.position in 1..3 }.sortedBy { it.position }
        if (podium.isNotEmpty()) {
            item {
                Text(
                    text = "PODIUM",
                    style = MaterialTheme.typography.labelMedium,
                    color = GoldFirst,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            podium.forEach { result ->
                item(key = "podium_${result.driver.id}") {
                    PodiumRow(result = result)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }

        // Rest of grid
        val restOfGrid = results.filter { it.position > 3 }
        if (restOfGrid.isNotEmpty()) {
            item {
                Text(
                    text = "CLASSIFICATION",
                    style = MaterialTheme.typography.labelMedium,
                    color = GhostGrey,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            itemsIndexed(
                items = restOfGrid,
                key = { _, result -> result.driver.id }
            ) { index, result ->
                ResultRow(result = result)
                if (index < restOfGrid.lastIndex) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp,
                    )
                }
            }
        }
    }
}

@Composable
private fun RaceHeader(
    raceName: String,
    circuitName: String,
    date: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .background(F1Red)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = raceName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = circuitName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = GhostGrey,
                )
            }
        }
    }
}

@Composable
private fun PodiumRow(result: RaceResult) {
    val podiumColor = when (result.position) {
        1 -> GoldFirst
        2 -> SilverSecond
        3 -> BronzeThird
        else -> MaterialTheme.colorScheme.onSurface
    }
    val hasFastestLap = result.fastestLap?.rank == 1

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Podium color accent
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    .background(podiumColor)
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Position
                Text(
                    text = "P${result.position}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = podiumColor,
                    modifier = Modifier.width(56.dp)
                )

                // Driver info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.driver.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = result.constructor.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (hasFastestLap) {
                            Text(
                                text = "  FASTEST LAP",
                                style = MaterialTheme.typography.labelSmall,
                                color = FastestLapPurple,
                            )
                        }
                    }
                }

                // Points + grid delta
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${result.points.toInt()} PTS",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    GridDelta(grid = result.grid, finish = result.position)
                }
            }
        }
    }
}

@Composable
private fun ResultRow(result: RaceResult) {
    val hasFastestLap = result.fastestLap?.rank == 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Position
        Text(
            text = result.positionText,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(32.dp)
        )

        // Driver info
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = result.driver.fullName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (hasFastestLap) {
                    Text(
                        text = "  FL",
                        style = MaterialTheme.typography.labelSmall,
                        color = FastestLapPurple,
                    )
                }
            }
            Text(
                text = result.constructor.name,
                style = MaterialTheme.typography.bodySmall,
                color = GhostGrey,
            )
        }

        // Points + status
        Column(horizontalAlignment = Alignment.End) {
            if (result.points > 0) {
                Text(
                    text = "${result.points.toInt()} PTS",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            if (result.status != "Finished") {
                Text(
                    text = result.status.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (result.status.contains("DNF", ignoreCase = true) ||
                        result.status.contains("Retired", ignoreCase = true))
                        MaterialTheme.colorScheme.error
                    else GhostGrey,
                )
            } else {
                GridDelta(grid = result.grid, finish = result.position)
            }
        }
    }
}

@Composable
private fun GridDelta(grid: Int, finish: Int) {
    val delta = grid - finish
    if (delta == 0 || grid == 0) return

    val (text, color) = if (delta > 0) {
        "+$delta" to GreenFlag
    } else {
        "$delta" to F1Red.copy(alpha = 0.7f)
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0F)
@Composable
private fun ResultsScreenPreview() {
    val raceWithResults = createRaceWithResults()
    F1SandboxTheme(darkTheme = true) {
        ResultsScreenContent(
            viewState = ResultsViewState(
                isLoading = false,
                isRefreshing = false,
                results = raceWithResults.results,
                raceName = raceWithResults.race.name,
                circuitName = raceWithResults.race.circuit.name,
                date = raceWithResults.race.date,
                errorMessage = null
            ),
            onRefresh = {},
            onRetry = {}
        )
    }
}
