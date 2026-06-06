package com.parrishdev.f1sandbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.parrishdev.driver.contracts.DriverDetailsRoute
import com.parrishdev.driver.contracts.DriversListRoute
import com.parrishdev.driver.drivers.driverGraph
import com.parrishdev.navigation.SharedViewModel
import com.parrishdev.race.contracts.RaceGraph
import com.parrishdev.race.contracts.RaceHomeScreen
import com.parrishdev.race.contracts.RaceResultsScreen
import com.parrishdev.race.home.raceGraph
import com.parrishdev.settings.contracts.SettingsScreen
import com.parrishdev.settings.feature.settingsGraph
import com.parrishdev.settings.store.SettingsStore
import com.parrishdev.ui.F1SandboxTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val sharedViewModel: SharedViewModel by viewModels()

    @Inject
    lateinit var settingsStore: SettingsStore

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by settingsStore.streamIsDarkMode()
                .collectAsStateWithLifecycle(initialValue = true)

            F1SandboxTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val selectedSeason by sharedViewModel.selectedSeason.collectAsStateWithLifecycle()
                val showSeasonSelector = currentDestination?.hasRoute<RaceHomeScreen>() == true ||
                        currentDestination?.hasRoute<DriversListRoute>() == true

                val showTopBar = currentDestination?.hasRoute<DriverDetailsRoute>() != true
                val showBottomBar = currentDestination?.hasRoute<DriverDetailsRoute>() != true &&
                        currentDestination?.hasRoute<RaceResultsScreen>() != true

                val canGoBack = navBackStackEntry?.destination?.parent?.startDestinationRoute !=
                    navBackStackEntry?.destination?.route

                val title = when {
                    currentDestination?.hasRoute<RaceHomeScreen>() == true -> "RACES"
                    currentDestination?.hasRoute<RaceResultsScreen>() == true -> "RACE RESULTS"
                    currentDestination?.hasRoute<DriversListRoute>() == true -> "DRIVERS"
                    currentDestination?.hasRoute<DriverDetailsRoute>() == true -> "DRIVER PROFILE"
                    currentDestination?.hasRoute<SettingsScreen>() == true -> "SETTINGS"
                    else -> "F1 SANDBOX"
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        if (showTopBar) {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                },
                                navigationIcon = {
                                    if (canGoBack) {
                                        IconButton(onClick = { navController.navigateUp() }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Navigate back"
                                            )
                                        }
                                    }
                                },
                                actions = {
                                    if (showSeasonSelector) {
                                        var expanded by remember { mutableStateOf(false) }
                                        Box {
                                            TextButton(onClick = { expanded = true }) {
                                                Text(
                                                    text = selectedSeason.toString(),
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Icon(
                                                    imageVector = Icons.Default.ArrowDropDown,
                                                    contentDescription = "Select season",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false }
                                            ) {
                                                listOf(2023, 2024, 2025, 2026).forEach { season ->
                                                    DropdownMenuItem(
                                                        text = { Text(season.toString()) },
                                                        onClick = {
                                                            sharedViewModel.updateSeason(season)
                                                            expanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.background,
                                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                                ),
                            )
                        }
                    },
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(navController)
                        }
                    }
                ) { innerPadding ->
                    AppNavigationHost(innerPadding, navController, selectedSeason)
                }
            }
        }
    }
}

@Composable
fun AppNavigationHost(
    paddingValues: PaddingValues,
    navController: NavHostController,
    selectedSeason: Int,
) {
    NavHost(
        navController = navController,
        startDestination = RaceGraph,
        modifier = Modifier.padding(paddingValues)
    ) {
        raceGraph(rootNavController = navController, selectedSeason = selectedSeason)
        settingsGraph()
        driverGraph(rootNavController = navController, selectedSeason = selectedSeason)
    }
}
