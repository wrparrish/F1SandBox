package com.parrishdev.driver.drivers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDetailScreen(
    onBackClick: () -> Unit,
    viewModel: DriverDetailViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewState by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(lifecycleOwner) {
        viewModel.onStartObserving(lifecycleOwner)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Driver Profile", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        DriverDetailContent(
            viewState = viewState,
            onRefresh = viewModel::onRefresh,
            onRetry = viewModel::onRetry,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DriverDetailContent(
    viewState: DriverDetailViewState,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        viewState.isLoading -> Loading(modifier = modifier)

        viewState.errorMessage != null && viewState.driver == null -> {
            Error(errorMessage = viewState.errorMessage, onRetry = onRetry, modifier = modifier)
        }

        viewState.driver != null -> {
            PullToRefreshBox(
                isRefreshing = viewState.isRefreshing,
                onRefresh = onRefresh,
                modifier = modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    val driver = viewState.driver
                    val teamColor = parseTeamColor(driver.teamColour)

                    // Hero profile headshot + team backdrop
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        teamColor.copy(alpha = 0.4f),
                                        MaterialTheme.colorScheme.background
                                    )
                                )
                            )
                    ) {
                        // Driver Big Headshot Image
                        DriverLargeHeadshot(
                            headshotUrl = driver.headshotUrl,
                            contentDescription = "${driver.fullName} Profile",
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .height(260.dp)
                                .width(200.dp)
                        )
                    }

                    // Main info card
                    Card(
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 24.dp)
                        ) {
                            // Driver Initials + Country Flag Label + Number
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                MaterialTheme.colorScheme.surfaceVariant,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = driver.nameAcronym,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = driver.countryCode,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = GhostGrey
                                    )
                                }

                                Text(
                                    text = "#${driver.driverNumber}",
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontWeight = FontWeight.Black
                                    ),
                                    color = teamColor
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Driver Full Name
                            Text(
                                text = driver.fullName,
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Team Name with Color Strip
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(teamColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = driver.teamName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = teamColor
                                )
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            // Stats section
                            Text(
                                text = "SEASON STATISTICS",
                                style = MaterialTheme.typography.labelMedium,
                                color = GhostGrey,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                StatCard(
                                    label = "STANDING",
                                    value = if (driver.championshipPosition > 0) "P${driver.championshipPosition}" else "N/A",
                                    valueColor = when (driver.championshipPosition) {
                                        1 -> GoldFirst
                                        2 -> SilverSecond
                                        3 -> BronzeThird
                                        else -> MaterialTheme.colorScheme.onSurface
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    label = "POINTS",
                                    value = driver.championshipPoints.toInt().toString(),
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    label = "WINS",
                                    value = driver.wins.toString(),
                                    valueColor = if (driver.wins > 0) GoldFirst else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = GhostGrey
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = valueColor
            )
        }
    }
}

@Composable
private fun DriverLargeHeadshot(
    headshotUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = headshotUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit
    ) {
        val state = painter.state.collectAsState()
        when (state.value) {
            is AsyncImagePainter.State.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = F1Red,
                        strokeWidth = 3.dp,
                    )
                }
            }

            is AsyncImagePainter.State.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        imageVector = Icons.Default.Person,
                        contentDescription = contentDescription,
                        modifier = Modifier.size(80.dp),
                        alpha = 0.3f
                    )
                }
            }

            else -> SubcomposeAsyncImageContent()
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

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0F)
@Composable
private fun DriverDetailPreview() {
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
        DriverDetailContent(
            viewState = DriverDetailViewState(
                isLoading = false,
                isRefreshing = false,
                driver = driver,
                errorMessage = null
            ),
            onRefresh = {},
            onRetry = {}
        )
    }
}
