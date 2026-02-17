package com.rampu.erasmapp.main

import android.util.Log
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rampu.erasmapp.R
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
import com.rampu.erasmapp.common.ui.components.UserAvatar
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainGraph(
    onSignOut: () -> Unit

) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var topBarState by remember { mutableStateOf<TopBarState?>(null) }
    var topBarOwnerId by remember { mutableStateOf<String?>(null) }
    val setTopBar: (String, TopBarState?) -> Unit = { ownerId, state ->
        if (state == null) {
            if (topBarOwnerId == ownerId) {
                topBarOwnerId = null
                topBarState = null
            }
        } else {
            topBarOwnerId = ownerId
            topBarState = state
        }
    }
    val openProfile = {
        navController.navigate(ProfileRoute) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val topLevelDestinations = listOf(
        TopLevelDestination("Home", Icons.Filled.Home, HomeRoute),
        TopLevelDestination("Schedule", Icons.Filled.DateRange, ScheduleRoute),
        TopLevelDestination("Map", Icons.Filled.Place, NavigationRoute),
        TopLevelDestination("Channels", Icons.AutoMirrored.Filled.List, ChannelsRoute),
        TopLevelDestination("News", Icons.Filled.Notifications, NewsRoute)
    )

    val currentTopLevelDestination = topLevelDestinations.firstOrNull { destination ->
        routeMatches(currentRoute, destination.route)
    }
    val showProfileAction = currentTopLevelDestination != null
    val currentEntryId = navBackStackEntry?.id
    val activeTopBarState = if (topBarOwnerId == currentEntryId) topBarState else null
    val profileLabel = stringResource(R.string.profile)
    val resolvedTopBarState = when {
        activeTopBarState != null -> activeTopBarState
        currentTopLevelDestination != null -> TopBarState(
            title = currentTopLevelDestination.label
        )
        routeMatches(currentRoute, ProfileRoute) -> TopBarState(
            title = profileLabel,
            onNavigateUp = { navController.navigateUp() }
        )
        else -> null
    }

    val showBottomBar = topLevelDestinations.any { destination ->
        routeMatches(currentRoute, destination.route)
    }

    ErasMappTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                resolvedTopBarState?.let { state ->
                    TopAppBar(
                        title = {
                            Text(
                                text = state.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            if (state.onNavigateUp != null) {
                                IconButton(onClick = state.onNavigateUp) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.navigate_up)
                                    )
                                }
                            }
                        },
                        actions = {
                            state.actions(this)
                            if (showProfileAction) {
                                ProfileActionButton(onClick = openProfile)
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (showBottomBar) {
                    MainBottomBar(
                        items = topLevelDestinations,
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
            }
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
                        onGoToSchedule = { navController.navigate(ScheduleRoute) },
                        onGoToEventCalendar = { navController.navigate(EventCalendarRoute) },
                        onGoToChannels = { navController.navigate(ChannelsRoute) },
                        onGoToFOI = { navController.navigate(FOIRoute) }
                    )
                }
                composable<ScheduleRoute> { backstackEntry ->
                    ScheduleScreen(
                        setTopBar = setTopBar,
                        topBarOwnerId = backstackEntry.id
                    )
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
                        setTopBar = setTopBar,
                        topBarOwnerId = backstackEntry.id,
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

                composable<NewsRoute> { backstackEntry ->
                    val vm: NewsViewModel = koinViewModel()
                    val state = vm.uiState.collectAsStateWithLifecycle()

                    NewsScreen(
                        setTopBar = setTopBar,
                        topBarOwnerId = backstackEntry.id,
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

data class TopBarState(
    val title: String,
    val onNavigateUp: (() -> Unit)? = null,
    val actions: @Composable RowScope.() -> Unit = {}
)

private data class TopLevelDestination(
    val label: String,
    val icon: ImageVector,
    val route: Any
)

@Composable
private fun ProfileActionButton(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        UserAvatar(
            label = stringResource(R.string.profile),
            icon = Icons.Filled.Person,
            size = 32.dp
        )
    }
}

@Composable
private fun MainBottomBar(
    items: List<TopLevelDestination>,
    currentRoute: String?,
    onNavigate: (Any) -> Unit
) {
    NavigationBar {
        items.forEach { item ->
            val isSelected = routeMatches(currentRoute, item.route)
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

private fun routeMatches(currentRoute: String?, route: Any): Boolean {
    if (currentRoute.isNullOrBlank()) return false
    val qualifiedName = route::class.qualifiedName
    val simpleName = route::class.simpleName
    return currentRoute == qualifiedName ||
        (simpleName != null && currentRoute.endsWith(simpleName))
}
