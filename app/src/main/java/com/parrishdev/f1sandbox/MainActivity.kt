package com.parrishdev.f1sandbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.parrishdev.drivers.driversGraph
import com.parrishdev.home.homeGraph
import com.parrishdev.settings.settingsGraph
import com.parrishdev.ui.F1SandboxTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            F1SandboxTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController) }) { innerPadding ->
                    AppNavigationHost(innerPadding, navController)
                }
            }
        }
    }
}

@Composable
fun AppNavigationHost(paddingValues: PaddingValues, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME_GRAPH,
        modifier = Modifier.padding(paddingValues)
    ) {
        homeGraph()
        settingsGraph()
        driversGraph()
    }

}

