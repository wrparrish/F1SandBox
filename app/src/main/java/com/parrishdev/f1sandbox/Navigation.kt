package com.parrishdev.f1sandbox

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.parrishdev.driver.contracts.DriverGraph
import com.parrishdev.race.contracts.RaceGraph
import com.parrishdev.settings.contracts.SettingsGraph
import kotlin.reflect.KClass

/**
 * Bottom navigation items with type-safe route references.
 *
 * Navigation Compose 2.8+ requires both:
 * - KClass for hierarchy-based selection detection (hasRoute)
 * - Route instance for actual navigation (navigate)
 */
sealed class BottomNavItem<T : Any>(
    val routeClass: KClass<T>,
    val route: T,
    val title: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem<RaceGraph>(RaceGraph::class, RaceGraph, "Races", Icons.Filled.Home)
    data object Drivers : BottomNavItem<DriverGraph>(DriverGraph::class, DriverGraph, "Drivers", Icons.Filled.Person)
    data object Settings : BottomNavItem<SettingsGraph>(SettingsGraph::class, SettingsGraph, "Settings", Icons.Filled.Settings)
}

/**
 * Modern bottom navigation bar following Navigation Compose 2.8+ best practices.
 *
 * Key improvements over basic implementation:
 * - Uses hierarchy-based selection detection (handles nested destinations correctly)
 * - Proper popUpTo behavior that preserves state across tab switches
 * - Single top launch to prevent multiple instances
 */
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Drivers,
        BottomNavItem.Settings
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            // Use hierarchy to check if this tab contains the current destination
            // This correctly highlights the tab even when on nested screens
            val selected = currentDestination?.hierarchy?.any {
                it.hasRoute(item.routeClass)
            } == true

            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = selected,
                onClick = {
                    // Navigate using the route instance (not KClass)
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to avoid
                        // building up a large back stack when switching tabs
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected tab
                        restoreState = true
                    }
                }
            )
        }
    }
}
