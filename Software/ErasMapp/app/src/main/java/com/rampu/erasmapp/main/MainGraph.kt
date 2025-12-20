package com.rampu.erasmapp.main

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rampu.erasmapp.eventCalendar.ui.EventCalendarScreen
import com.rampu.erasmapp.schedule.ui.ScheduleScreen
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun MainGraph(
    onSignOut: () -> Unit

){
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomItems = listOf(
        BottomNavItem("Home", Icons.Filled.Home, HomeRoute),
        BottomNavItem("Schedule", Icons.Filled.DateRange, ScheduleRoute),
        BottomNavItem("Map", Icons.Filled.Place, MapRoute),
        BottomNavItem("Profile", Icons.Filled.Person, ProfileRoute)
    )

    ErasMappTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                MainBottomBar(
                    items = bottomItems,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = HomeRoute,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                        bottom = 80.dp
                    )
            ){
                composable<HomeRoute> {
                    HomeScreen(
                        onSignOut = onSignOut,
                        onGoToSchedule = { navController.navigate(ScheduleRoute) },
                        onGoToEventCalendar = { navController.navigate(EventCalendarRoute) },
                        onGoToAdmin = { navController.navigate(AdminRoute) }
                    )
                }
                composable<ScheduleRoute> {
                    ScheduleScreen(onBack = { navController.popBackStack() })
                }
                composable<EventCalendarRoute> {
                    EventCalendarScreen(onBack = { navController.popBackStack() })
                }
                composable<MapRoute> {
                    MapScreen()
                }
                composable<ProfileRoute> {
                    ProfileScreen(
                        onSignOut = onSignOut,
                        onGoToAdmin = { navController.navigate(AdminRoute) }
                    )
                }

            }
        }
    }


}

private data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Any
)

@Composable
private fun MainBottomBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onNavigate: (Any) -> Unit
) {
    NavigationBar {
        items.forEach { item ->
            val simpleName = item.route::class.simpleName
            val isSelected = currentRoute == item.route::class.qualifiedName ||
                (simpleName != null && currentRoute?.endsWith(simpleName) == true)
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) }
            )
        }
    }
}
