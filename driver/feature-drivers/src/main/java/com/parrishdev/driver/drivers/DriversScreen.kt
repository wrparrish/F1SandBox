@file:OptIn(ExperimentalMaterial3Api::class)

package com.parrishdev.driver.drivers

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.*
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.parrishdev.driver.model.Driver
import com.parrishdev.ui.BronzeThird
import com.parrishdev.ui.F1Red
import com.parrishdev.ui.F1SandboxTheme
import com.parrishdev.ui.GhostGrey
import com.parrishdev.ui.GoldFirst
import com.parrishdev.ui.SilverSecond
import com.parrishdev.ui.common.Error
import com.parrishdev.ui.common.Loading
import com.parrishdev.ui.common.singleClickable

@Composable
fun DriversScreen(
    selectedSeason: Int,
    onNavigateToDetails: (driverNumber: Int, season: Int) -> Unit,
    viewModel: DriversViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewState by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(selectedSeason) {
        viewModel.onSeasonChanged(selectedSeason)
    }

    // Observe state changes

    LaunchedEffect(lifecycleOwner) {
        viewModel.onStartObserving(lifecycleOwner)
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is DriversEvent.NavigateToDriverDetails -> {
                    onNavigateToDetails(event.driverNumber, event.season)
                }

                is DriversEvent.ShowError -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        withDismissAction = true
                    )
                    when (result) {
                        Dismissed -> {

                        }
                        ActionPerformed ->  {

                        }
                    }
                }
            }
        }
    }

    DriversScreenContent(
        viewState = viewState,
        onDriverSelected = viewModel::onDriverSelected,
        onRefresh = viewModel::onRefresh,
        onRetry = viewModel::onRetry
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DriversScreenContent(
    viewState: DriversViewState,
    onDriverSelected: (Driver) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit
) {
    when {
        viewState.isLoading -> Loading()

        viewState.errorMessage != null && viewState.drivers.isEmpty() -> {
            Error(errorMessage = viewState.errorMessage, onRetry = onRetry)
        }

        viewState.showEmptyState -> EmptyState(
            isRefreshing = viewState.isRefreshing,
            onRefresh = onRefresh
        )

        else -> {
            PullToRefreshBox(
                isRefreshing = viewState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                DriversList(drivers = viewState.drivers, onDriverSelected = onDriverSelected)
            }
        }
    }
}

@Composable
private fun DriversList(
    drivers: List<Driver>,
    onDriverSelected: (Driver) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = drivers, key = { it.driverNumber }) { driver ->
            DriverCard(driver = driver, onClick = { onDriverSelected(driver) })
        }
    }
}

@Composable
private fun DriverCard(
    driver: Driver,
    onClick: () -> Unit
) {
    val teamColor = parseTeamColor(driver.teamColour)

    Card(
        shape = RoundedCornerShape(14.dp),
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
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Team color accent strip
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
                    .background(teamColor)
            )

            // Standing Position (on the left)
            Column(
                modifier = Modifier
                    .width(56.dp)
                    .padding(start = 12.dp, end = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (driver.championshipPosition > 0) driver.championshipPosition.toString() else "-",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = when (driver.championshipPosition) {
                        1 -> GoldFirst
                        2 -> SilverSecond
                        3 -> BronzeThird
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = "POS",
                    style = MaterialTheme.typography.labelSmall,
                    color = GhostGrey
                )
            }

            // Headshot
            DriverHeadshot(
                headshotUrl = driver.headshotUrl,
                contentDescription = "${driver.fullName} headshot",
                modifier = Modifier
                    .height(80.dp)
                    .width(64.dp)
                    .padding(vertical = 8.dp)
            )

            // Driver info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = driver.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "#${driver.driverNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GhostGrey,
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = driver.teamName,
                    style = MaterialTheme.typography.bodySmall,
                    color = teamColor,
                )
                if (driver.wins > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (driver.wins == 1) "1 win" else "${driver.wins} wins",
                        style = MaterialTheme.typography.labelSmall,
                        color = GhostGrey,
                    )
                }
            }

            // Points (on the right)
            Column(
                modifier = Modifier.padding(end = 16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = driver.championshipPoints.toInt().toString(),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "PTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = GhostGrey
                )
            }
        }
    }
}

private fun parseTeamColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor("#$hex"))
    } catch (_: Exception) {
        Color(0xFFAAAAAC)
    }
}

@Composable
private fun DriverHeadshot(
    headshotUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = headshotUrl,
        contentDescription = contentDescription,
        modifier = modifier.clip(RoundedCornerShape(8.dp))
    ) {
        val state = painter.state.collectAsState()
        when (state.value) {
            is AsyncImagePainter.State.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = F1Red,
                        strokeWidth = 2.dp,
                    )
                }
            }

            is AsyncImagePainter.State.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        imageVector = Icons.Default.Person,
                        contentDescription = contentDescription,
                        modifier = Modifier.size(36.dp),
                        alpha = 0.4f,
                    )
                }
            }

            else -> SubcomposeAsyncImageContent()
        }
    }
}

@Composable
private fun EmptyState(isRefreshing: Boolean, onRefresh: () -> Unit) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "NO DRIVERS FOUND",
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
private fun DriverCardPreview() {
    val driver = Driver(
        driverNumber = 4,
        firstName = "Lando",
        lastName = "Norris",
        fullName = "Lando Norris",
        nameAcronym = "NOR",
        countryCode = "GB",
        teamName = "McLaren",
        teamColour = "F47600",
        headshotUrl = null,
        broadcastName = "L NORRIS",
        sessionKey = 9158,
        championshipPosition = 1,
        championshipPoints = 180f,
        wins = 4
    )
    F1SandboxTheme(darkTheme = true) {
        DriverCard(driver = driver, onClick = {})
    }
}
