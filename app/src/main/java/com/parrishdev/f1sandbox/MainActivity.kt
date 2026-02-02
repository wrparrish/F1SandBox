package com.parrishdev.f1sandbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.parrishdev.driver.drivers.driverGraph
import com.parrishdev.navigation.SharedViewModel
import com.parrishdev.race.contracts.RaceGraph
import com.parrishdev.race.home.raceGraph
import com.parrishdev.settings.feature.settingsGraph
import com.parrishdev.ui.F1SandboxTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            F1SandboxTheme {
                val navController = rememberNavController()
                val currentTitle = sharedViewModel.currentTitle.collectAsState()
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        @OptIn(ExperimentalMaterial3Api::class)
                        TopAppBar(
                            title = {
                                Text(currentTitle.value)
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    navController.navigateUp()
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Tap to go back"
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = { BottomNavigationBar(navController) }) { innerPadding ->
                    AppNavigationHost(innerPadding, navController)
                }
            }
        }
    }
}

/**
 * Main navigation host with type-safe routes.
 * Uses Navigation Compose 2.8+ typed navigation API.
 */
@Composable
fun AppNavigationHost(
    paddingValues: PaddingValues,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = RaceGraph,
        modifier = Modifier.padding(paddingValues)
    ) {
        raceGraph(rootNavController = navController)
        settingsGraph()
        driverGraph(rootNavController = navController)
    }
}
