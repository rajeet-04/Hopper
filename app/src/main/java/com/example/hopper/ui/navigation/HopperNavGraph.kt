package com.example.hopper.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hopper.ui.map.MapLibreEngineController
import com.example.hopper.ui.map.MapScreen
import com.example.hopper.ui.nearme.NearMeScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Map : Screen("map", "Map", Icons.Default.Map)
    data object NearMe : Screen("near_me", "Near Me", Icons.Default.NearMe)
    data object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    data object Explore : Screen("explore", "Explore", Icons.Default.Explore)
}

val bottomNavItems = listOf(Screen.Map, Screen.NearMe, Screen.Calendar, Screen.Explore)

@Composable
fun HopperNavGraph(
    mapEngineController: MapLibreEngineController
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Map.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Map.route) {
                MapScreen(
                    mapEngineController = mapEngineController,
                    onPandalClick = { pandalId ->
                        // Navigate to pandal detail
                    }
                )
            }
            composable(Screen.NearMe.route) {
                NearMeScreen(
                    onPandalClick = { pandalId ->
                        // Navigate to pandal detail
                    }
                )
            }
            composable(Screen.Calendar.route) {
                // CalendarScreen placeholder
                Text("Calendar")
            }
            composable(Screen.Explore.route) {
                // Explore screen placeholder
                Text("Explore")
            }
        }
    }
}
