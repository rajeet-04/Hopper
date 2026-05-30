package com.example.hopper.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hopper.ui.calendar.CalendarScreen
import com.example.hopper.ui.itinerary.ItineraryScreen
import com.example.hopper.ui.leaderboard.LeaderboardScreen
import com.example.hopper.ui.lighttrail.LightTrailScreen
import com.example.hopper.ui.map.MapLibreEngineController
import com.example.hopper.ui.map.MapScreen
import com.example.hopper.ui.nearme.NearMeScreen
import com.example.hopper.ui.oralhistory.OralHistoryScreen
import com.example.hopper.ui.ritual.RitualGuideScreen
import com.example.hopper.ui.volunteer.VolunteerScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Map : Screen("map", "Map", Icons.Default.Map)
    data object NearMe : Screen("near_me", "Near Me", Icons.Default.NearMe)
    data object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    data object Explore : Screen("explore", "Explore", Icons.Default.Explore)
}

val bottomNavItems = listOf(Screen.Map, Screen.NearMe, Screen.Calendar, Screen.Explore)

/** Secondary destinations reachable from the Explore hub. */
private data class ExploreDestination(val route: String, val title: String, val subtitle: String)

private val exploreDestinations = listOf(
    ExploreDestination("itinerary", "Itinerary Planner", "Build an optimized pandal-hopping route"),
    ExploreDestination("light_trail", "Light Trail", "Chandannagar illuminated installations (Jagaddhatri)"),
    ExploreDestination("oral_history", "Oral Histories", "Community stories tied to pandals"),
    ExploreDestination("ritual_guide", "Ritual Guides", "Step-by-step ritual instructions"),
    ExploreDestination("volunteer", "Volunteer", "Sign up for festival volunteer shifts"),
    ExploreDestination("leaderboard", "Leaderboard", "Community reporter reputation")
)

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
                    onPandalClick = { }
                )
            }
            composable(Screen.NearMe.route) {
                NearMeScreen(onPandalClick = { })
            }
            composable(Screen.Calendar.route) {
                CalendarScreen()
            }
            composable(Screen.Explore.route) {
                ExploreScreen(navController = navController)
            }
            composable("itinerary") { ItineraryScreen() }
            composable("light_trail") { LightTrailScreen() }
            composable("oral_history") { OralHistoryScreen() }
            composable("ritual_guide") { RitualGuideScreen() }
            composable("volunteer") { VolunteerScreen() }
            composable("leaderboard") { LeaderboardScreen() }
        }
    }
}

@Composable
private fun ExploreScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Explore",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        LazyColumn(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(exploreDestinations) { destination ->
                Card(
                    modifier = Modifier.fillMaxSize(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    onClick = { navController.navigate(destination.route) }
                ) {
                    ListItem(
                        headlineContent = { Text(destination.title) },
                        supportingContent = { Text(destination.subtitle) },
                        trailingContent = {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }
}
