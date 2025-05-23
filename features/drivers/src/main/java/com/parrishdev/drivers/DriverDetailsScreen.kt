package com.parrishdev.drivers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.parrishdev.model.DriverRaceResult
import com.parrishdev.ui.common.Error
import com.parrishdev.ui.common.Loading

@Composable
fun DriverDetailsScreen(
    navController: NavController, // Keep for potential future back navigation, etc.
    viewModel: DriverDetailsViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsState().value

    when {
        state.isLoading -> Loading()
        state.errorMessage != null -> Error(errorMessage = state.errorMessage)
        else -> {
            DriverResultsList(
                driverId = state.driverId ?: "Driver", // Fallback title
                results = state.driverResults
            )
        }
    }
}

@Composable
fun DriverResultsList(driverId: String, results: List<DriverRaceResult>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(8.dp)) {
        Text(
            text = "Results for ${driverId.uppercase()}", // Display the driverId being viewed
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(results) { result ->
                RaceResultCard(result = result)
            }
        }
    }
}

@Composable
fun RaceResultCard(result: DriverRaceResult, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${result.season} ${result.raceName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Date: ${result.date}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Round: ${result.round}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Team: ${result.constructorName}", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                InfoPill(label = "Grid", value = result.grid)
                InfoPill(label = "Finish", value = result.position, highlight = true)
                InfoPill(label = "Points", value = result.points)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Status: ${result.status}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
fun InfoPill(label: String, value: String, highlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
            color = if (highlight && (value == "1" || value == "P1")) Color(0xFFD4AF37) else MaterialTheme.colorScheme.onSurface // Gold for P1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RaceResultCardPreview() {
    val sampleResult = DriverRaceResult(
        season = "2023",
        round = "10",
        raceName = "Austrian Grand Prix",
        date = "2023-07-02",
        constructorName = "Red Bull Racing",
        grid = "1",
        position = "1",
        points = "26", // Including fastest lap point
        status = "Finished",
        driverId = "max_verstappen",
        circuitName = "Red Bull Ring"
    )
    MaterialTheme { // Wrap with MaterialTheme for preview
        RaceResultCard(result = sampleResult, modifier = Modifier.padding(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DriverResultsListPreview() {
    val sampleResults = listOf(
        DriverRaceResult("2023", "1", "Bahrain Grand Prix", "2023-03-05", "Red Bull", "1", "1", "25", "Finished", "VER", "Sakhir"),
        DriverRaceResult("2023", "2", "Saudi Arabian Grand Prix", "2023-03-19", "Red Bull", "15", "2", "19", "Finished", "VER", "Jeddah"),
        DriverRaceResult("2022", "5", "Miami Grand Prix", "2022-05-08", "Ferrari", "2", "DNF", "0", "Collision", "LEC", "Miami")

    )
    MaterialTheme {
         DriverResultsList(driverId = "VER", results = sampleResults)
    }
}
