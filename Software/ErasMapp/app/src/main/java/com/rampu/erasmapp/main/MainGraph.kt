package com.rampu.erasmapp.main

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rampu.erasmapp.adminConsole.AdminConsoleScreen
import com.rampu.erasmapp.adminConsole.AdminEventsScreen
import com.rampu.erasmapp.adminConsole.AdminNewsScreen
import com.rampu.erasmapp.adminConsole.AdminRoomsScreen
import com.rampu.erasmapp.channels.ui.channels.ChannelsScreen
import com.rampu.erasmapp.channels.ui.channels.ChannelsViewModel
import com.rampu.erasmapp.channels.ui.questions.QuestionsScreen
import com.rampu.erasmapp.channels.ui.questions.QuestionsViewModel
import com.rampu.erasmapp.channels.ui.threads.ThreadScreen
import com.rampu.erasmapp.channels.ui.threads.ThreadViewModel
import com.rampu.erasmapp.eventCalendar.ui.EventCalendarScreen
import com.rampu.erasmapp.foibuildings.BuildingScreen
import com.rampu.erasmapp.navigation.MapScreen
import com.rampu.erasmapp.news.ui.NewsDetailScreen
import com.rampu.erasmapp.news.ui.NewsScreen
import com.rampu.erasmapp.news.ui.NewsViewModel
import com.rampu.erasmapp.schedule.ui.ScheduleScreen
import com.rampu.erasmapp.ui.theme.ErasMappTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MainGraph(
    onSignOut: () -> Unit

) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    //val currentRoute = navBackStackEntry?.destination?.route

    val currentRoute = navController.currentBackStackEntryAsState()

//    val bottomItems = listOf(
//        BottomNavItem("Home", Icons.Filled.Home, HomeRoute),
//        BottomNavItem("Schedule", Icons.Filled.DateRange, ScheduleRoute),
//        BottomNavItem("Map", Icons.Filled.Place, MapRoute),
//        BottomNavItem("Profile", Icons.Filled.Person, ProfileRoute)
//    )

    ErasMappTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
//           bottomBar = {
//                MainBottomBar(
//                    items = bottomItems,
//                    currentRoute = currentRoute,
//                    onNavigate = { route ->
//                        navController.navigate(route) {
//                            popUpTo(navController.graph.findStartDestination().id) {
//                                saveState = true
//                            }
//                            launchSingleTop = true
//                            restoreState = true
//                        }
//                    }
//                )
//                }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = HomeRoute,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable<HomeRoute> {
                    HomeScreen(
                        onSignOut = onSignOut,
                        onGoToSchedule = { navController.navigate(ScheduleRoute) },
                        onGoToEventCalendar = { navController.navigate(EventCalendarRoute) },
                        onGoToAdmin = { navController.navigate(AdminRoute) },
                        onGoToChannels = { navController.navigate(ChannelsRoute) },
                        onGoToFOI = { navController.navigate(FOIRoute) },
                        onGoToNavigation = { navController.navigate(NavigationRoute) },
                        onGoToNews = { navController.navigate(NewsRoute) }
                    )
                }
                composable<ScheduleRoute> {
                    ScheduleScreen(onBack = { navController.popBackStack() })
                }
                composable<EventCalendarRoute> {
                    EventCalendarScreen(onBack = { navController.popBackStack() })
                }
                composable<ProfileRoute> {
                    ProfileScreen(
                        onSignOut = onSignOut,
                        onGoToAdmin = { navController.navigate(AdminRoute) }
                    )
                }
                composable<AdminRoute> {
                    AdminConsoleScreen(
                        onManageEvents = { navController.navigate(AdminEventsRoute) },
                        onManageRooms = { navController.navigate(AdminRoomsRoute) },
                        onManageNews = { navController.navigate(AdminNewsRoute) }
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

                composable<FOIRoute> {
                    Log.d("Deb", "FOIRoute composable called")
                    BuildingScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable<NavigationRoute> {
                    MapScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable<ChannelsRoute> {
                    val vm: ChannelsViewModel = koinViewModel()
                    val state = vm.uiState.collectAsStateWithLifecycle()

                    ChannelsScreen(
                        onBack = { navController.popBackStack() },
                        onEvent = vm::onEvent,
                        state = state.value,
                        onChannelSelected = { id, title ->
                            navController.navigate(
                                QuestionsRoute(
                                    id,
                                    title
                                )
                            )
                        }
                    )
                }

                composable<QuestionsRoute> { backstackEntry ->
                    val route = backstackEntry.toRoute<QuestionsRoute>()
                    val channelId = route.channelId
                    val channelTitle = route.channelTitle
                    val vm: QuestionsViewModel =
                        koinViewModel(parameters = { parametersOf(channelId, channelTitle) })
                    val state = vm.uiState.collectAsStateWithLifecycle()

                    QuestionsScreen(
                        channelId = channelId,
                        channelTitle = channelTitle,
                        onBack = { navController.popBackStack() },
                        onOpenQuestion = { questionId ->
                            navController.navigate(
                                ThreadRoute(channelId, channelTitle, questionId)
                            )
                        },
                        onEvent = vm::onEvent,
                        state = state.value
                    )
                }

                composable<ThreadRoute> { backstackEntry ->
                    val route = backstackEntry.toRoute<ThreadRoute>()
                    val channelId = route.channelId
                    val channelTitle = route.channelTitle
                    val questionId = route.questionId
                    val vm: ThreadViewModel = koinViewModel(parameters = {
                        parametersOf(
                            channelId,
                            channelTitle,
                            questionId
                        )
                    })
                    val state = vm.uiState.collectAsStateWithLifecycle()

                    ThreadScreen(
                        onBack = { navController.popBackStack() },
                        onEvent = vm::onEvent,
                        state = state.value
                    )
                }

                composable<NewsRoute> {
                    val vm: NewsViewModel = koinViewModel()
                    val state = vm.uiState.collectAsStateWithLifecycle()

                    NewsScreen(
                        onBack = { navController.popBackStack() },
                        onEvent = vm::onEvent,
                        state = state.value,
                        onOpenNews = { newsId -> navController.navigate(NewsDetailRoute(newsId)) }
                    )
                }

                composable<NewsDetailRoute> { backstackEntry ->
                    val route = backstackEntry.toRoute<NewsDetailRoute>()
                    val vm: NewsViewModel = koinViewModel()
                    val state = vm.uiState.collectAsStateWithLifecycle()

                    NewsDetailScreen(
                        newsId = route.newsId,
                        onBack = { navController.popBackStack() },
                        onEvent = vm::onEvent,
                        state = state.value
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
