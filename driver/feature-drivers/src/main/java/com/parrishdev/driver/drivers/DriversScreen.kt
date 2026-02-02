package com.parrishdev.driver.drivers

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import com.parrishdev.ui.common.singleClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.parrishdev.driver.model.Driver
import com.parrishdev.ui.common.Error
import com.parrishdev.ui.common.Loading

/**
 * Drivers screen displaying list of F1 drivers.
 *
 * @param onNavigateToDetails Called when user selects a driver
 */
@Composable
fun DriversScreen(
    onNavigateToDetails: (driverNumber: Int) -> Unit,
    viewModel: DriversViewModel = hiltViewModel()
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
                is DriversEvent.NavigateToDriverDetails -> {
                    onNavigateToDetails(event.driverNumber)
                }
                is DriversEvent.ShowError -> {
                    // Could show snackbar here. Would need remember(snackbarstate) etc, + scaffold
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
        viewState.isLoading -> {
            Loading()
        }

        viewState.errorMessage != null && viewState.drivers.isEmpty() -> {
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
                DriversList(
                    drivers = viewState.drivers,
                    onDriverSelected = onDriverSelected
                )
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
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = drivers,
            key = { it.driverNumber }
        ) { driver ->
            DriverCard(
                driver = driver,
                onClick = { onDriverSelected(driver) }
            )
        }
    }
}

@Composable
private fun DriverCard(
    driver: Driver,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .singleClickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Driver headshot
            DriverHeadshot(
                headshotUrl = driver.headshotUrl,
                contentDescription = "${driver.fullName} headshot",
                modifier = Modifier
                    .height(100.dp)
                    .width(80.dp)
                    .padding(start = 8.dp)
            )

            // Driver info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = driver.fullName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = driver.teamName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = driver.countryCode,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Driver number
            Text(
                text = driver.driverNumber.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
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
        modifier = modifier
    ) {
        val state = painter.state.collectAsState()
        when (state.value) {
            is AsyncImagePainter.State.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            is AsyncImagePainter.State.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        imageVector = Icons.Default.Person,
                        contentDescription = contentDescription,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            else -> {
                SubcomposeAsyncImageContent()
            }
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
            text = "No drivers found",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DriverCardPreview() {
    val driver = Driver(
        driverNumber = 14,
        firstName = "Fernando",
        lastName = "Alonso",
        fullName = "Fernando Alonso",
        nameAcronym = "ALO",
        countryCode = "ES",
        teamName = "Aston Martin",
        teamColour = "006F62",
        headshotUrl = null,
        broadcastName = "F ALONSO",
        sessionKey = 9158
    )
    DriverCard(driver = driver, onClick = {})
}
