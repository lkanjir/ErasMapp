package com.rampu.erasmapp.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rampu.erasmapp.adminConsole.AdminConsoleScreen
import com.rampu.erasmapp.adminConsole.AdminEventsScreen
import com.rampu.erasmapp.adminConsole.AdminNewsScreen
import com.rampu.erasmapp.adminConsole.AdminRoomsScreen
import com.rampu.erasmapp.eventCalendar.ui.EventCalendarScreen
import com.rampu.erasmapp.schedule.ui.ScheduleScreen
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun MainGraph(
    onSignOut: () -> Unit

){
    val navController = rememberNavController()

    ErasMappTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = HomeRoute,
                modifier = Modifier.fillMaxSize().padding(innerPadding)
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
                composable<AdminRoute> {
                    AdminConsoleScreen(
                        onManageEvents = { navController.navigate(AdminEventsRoute) },
                        onManageRooms = { navController.navigate(AdminRoomsRoute) },
                        onManageNews = {navController.navigate(AdminNewsRoute) }
                    )
                }
                composable<AdminEventsRoute> {
                    AdminEventsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable<AdminRoomsRoute> {
                    AdminRoomsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable<AdminNewsRoute> {
                    AdminNewsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

            }
        }
    }


}
