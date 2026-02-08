package com.parrishdev.f1sandbox

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.parrishdev.driver.contracts.DriverGraph
import com.parrishdev.race.contracts.RaceGraph
import com.parrishdev.settings.contracts.SettingsGraph
import com.parrishdev.ui.F1Red
import com.parrishdev.ui.GhostGrey
import kotlin.reflect.KClass

sealed class BottomNavItem<T : Any>(
    val routeClass: KClass<T>,
    val route: T,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    data object Home : BottomNavItem<RaceGraph>(
        RaceGraph::class, RaceGraph, "RACES",
        Icons.Outlined.Home, Icons.Filled.Home
    )
    data object Drivers : BottomNavItem<DriverGraph>(
        DriverGraph::class, DriverGraph, "DRIVERS",
        Icons.Outlined.Person, Icons.Filled.Person
    )
    data object Settings : BottomNavItem<SettingsGraph>(
        SettingsGraph::class, SettingsGraph, "SETTINGS",
        Icons.Outlined.Settings, Icons.Filled.Settings
    )
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Drivers,
        BottomNavItem.Settings
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        tonalElevation = 0.dp,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any {
                it.hasRoute(item.routeClass)
            } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = F1Red,
                    selectedTextColor = F1Red,
                    unselectedIconColor = GhostGrey,
                    unselectedTextColor = GhostGrey,
                    indicatorColor = F1Red.copy(alpha = 0.12f),
                ),
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
