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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
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

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            F1SandboxTheme(darkTheme = true) {
                val navController = rememberNavController()
                val currentTitle = sharedViewModel.currentTitle.collectAsState()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val canGoBack = navBackStackEntry?.destination?.parent?.startDestinationRoute !=
                    navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = currentTitle.value.uppercase(),
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
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                titleContentColor = MaterialTheme.colorScheme.onBackground,
                                navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                            ),
                        )
                    },
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    AppNavigationHost(innerPadding, navController)
                }
            }
        }
    }
}

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
