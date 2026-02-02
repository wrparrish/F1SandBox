package com.parrishdev.race.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.parrishdev.race.model.Constructor
import com.parrishdev.race.model.RaceDriver
import com.parrishdev.race.model.RaceResult
import com.parrishdev.ui.common.Error
import com.parrishdev.ui.common.Loading

/**
 * Results screen displaying race results for a specific race.
 */
@Composable
fun ResultsScreen(
    viewModel: ResultsViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewState by viewModel.stateFlow.collectAsStateWithLifecycle()

    // Set up lifecycle-aware store observations
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
        viewState.isLoading -> {
            Loading()
        }

        viewState.errorMessage != null && viewState.results.isEmpty() -> {
            Error(
                errorMessage = viewState.errorMessage,
                onRetry = onRetry
            )
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
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header
        item {
            RaceHeader(
                raceName = raceName,
                circuitName = circuitName,
                date = date
            )
        }

        // Results
        itemsIndexed(
            items = results,
            key = { _, result -> result.driver.id }
        ) { index, result ->
            ResultRow(result = result)
            if (index < results.lastIndex) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = raceName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = circuitName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun ResultRow(result: RaceResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Position
        Text(
            text = result.positionText,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = when (result.position) {
                1 -> MaterialTheme.colorScheme.primary
                2 -> MaterialTheme.colorScheme.secondary
                3 -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.padding(end = 16.dp)
        )

        // Driver info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = result.driver.fullName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = result.constructor.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Points
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${result.points.toInt()} pts",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = result.status,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ResultRowPreview() {
    val result = RaceResult(
        position = 1,
        positionText = "1",
        points = 25f,
        driver = RaceDriver(
            id = "verstappen",
            permanentNumber = "1",
            code = "VER",
            givenName = "Max",
            familyName = "Verstappen",
            nationality = "Dutch",
            url = ""
        ),
        constructor = Constructor(
            id = "red_bull",
            name = "Red Bull Racing",
            nationality = "Austrian",
            url = ""
        ),
        grid = 1,
        laps = 57,
        status = "Finished",
        fastestLap = null
    )
    ResultRow(result = result)
}
