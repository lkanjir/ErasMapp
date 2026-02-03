package com.rampu.erasmapp.main


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rampu.erasmapp.common.ui.components.LoadingIndicator
import com.rampu.erasmapp.eventCalendar.data.EventCalendarRepository
import com.rampu.erasmapp.schedule.domain.ScheduleEvent
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    onSignOut: () -> Unit,
    onGoToSchedule: () -> Unit,
    onGoToEventCalendar: () -> Unit,
    onGoToAdmin: () -> Unit,
    onGoToChannels: () -> Unit,
    onGoToFOI : () -> Unit,
    onGoToNavigation : () -> Unit,
    onGoToNews: () -> Unit
){
    val homeViewModel: HomeViewModel = koinViewModel()
    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()

    val repository: EventCalendarRepository = koinInject()
    val adminFlow = remember(repository) { repository.observeAdminStatus() }
    val isAdmin by adminFlow.collectAsState(initial = false)

    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .padding(bottom=20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        item { Text("Welcome to ErasMapp!") }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        item {
            TodayScheduleCard(
                state = homeState,
                onGoToSchedule = onGoToSchedule
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
        item {
            Button(
                onClick = onSignOut
            ){
                Text("Sign out")
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
        item {
            Button(onClick = onGoToSchedule) {
                Text("Go to Schedule")
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
        item {
            Button(onClick = onGoToEventCalendar) {
                Text("Go to Event Calendar")
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
        item {
            Button(
                onClick = onGoToNavigation) {
                Text("Go to Navigation")
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
        item {
            Button(
                onClick = onGoToFOI) {
                Text("FOI Buildings")
            }
        }

        if (isAdmin) {
            item { Spacer(modifier = Modifier.height(20.dp)) }
            item {
                Button(onClick = onGoToAdmin) {
                    Text("Admin console")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
        item{
            Button(onClick = onGoToChannels) {
                Text("Go  to channels")
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
        item{
            Button(onClick = onGoToNews) {
                Text("Go  to news")
            }
        }

    }

}

@Composable
private fun TodayScheduleCard(
    state: HomeUiState,
    onGoToSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(0.9f),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Today's schedule",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            when {
                state.isScheduleLoading -> {
                    LoadingIndicator()
                }

                state.isSignedOut -> {
                    Text(
                        text = "Sign in to see your schedule.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                !state.scheduleErrorMessage.isNullOrBlank() -> {
                    Text(
                        text = state.scheduleErrorMessage ?: "Unable to load schedule.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                state.todaySchedule.isEmpty() -> {
                    Text(
                        text = "No events today.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                else -> {
                    state.todaySchedule.forEach { event ->
                        TodayScheduleRow(event = event)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onGoToSchedule) {
                    Text("Go to Schedule")
                }
            }
        }
    }
}

@Composable
private fun TodayScheduleRow(event: ScheduleEvent) {
    val categoryColor = categoryColorFor(event.category)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(6.dp)
                .height(44.dp)
                .background(categoryColor)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${event.startTime} - ${event.endTime}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (event.location.isNotBlank() && event.location != "-") {
            Text(
                text = event.location,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun categoryColorFor(category: String) = when (category.lowercase()) {
    "lecture" -> androidx.compose.ui.graphics.Color(0xFFE63946)
    "exercises" -> androidx.compose.ui.graphics.Color(0xFF2A9D8F)
    "mid-term" -> androidx.compose.ui.graphics.Color(0xFF1D3557)
    "final-exam" -> androidx.compose.ui.graphics.Color(0xFF457B9D)
    "seminary" -> androidx.compose.ui.graphics.Color(0xFFF4A261)
    "other" -> androidx.compose.ui.graphics.Color(0xFFBDBDBD)
    else -> androidx.compose.ui.graphics.Color(0xFFBDBDBD)
}
