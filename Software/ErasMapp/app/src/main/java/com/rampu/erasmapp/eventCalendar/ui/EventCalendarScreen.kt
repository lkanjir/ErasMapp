package com.rampu.erasmapp.eventCalendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.rampu.erasmapp.eventCalendar.domain.CalendarEvent
import org.koin.androidx.compose.koinViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun EventCalendarScreen(
    onBack: () -> Unit = {},
    viewModel: EventCalendarViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val events = uiState.events
    val selectedDate = uiState.selectedDate

    val startMonth = YearMonth.now().minusMonths(3)
    val endMonth = YearMonth.now().plusMonths(3)
    val currentMonth = remember { YearMonth.now() }
    val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    "Event Calendar",
                    fontSize = 42.sp,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.W600,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                MonthCalendar(
                    events = events,
                    calendarState = calendarState,
                    selectedDate = selectedDate
                ) { date -> viewModel.onDateSelected(date) }
                Spacer(modifier = Modifier.height(16.dp))
            }

            selectedDate?.let { date ->
                item {
                    Text(
                        text = "Events for: ${date.format(dateFormatter)}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                val todaysEvents = events.filter { it.date == date }
                if (todaysEvents.isEmpty()) {
                    item { Text("No events", style = MaterialTheme.typography.bodyMedium) }
                } else {
                    items(todaysEvents, key = { "${it.date}-${it.title}" }) { event ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = event.time,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = event.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = event.description,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MonthCalendar(
    events: List<CalendarEvent>,
    calendarState: CalendarState,
    selectedDate: LocalDate?,
    onDayClick: (LocalDate) -> Unit
) {
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek = calendarState.firstDayOfWeek) }

    HorizontalCalendar(
        state = calendarState,
        monthHeader = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysOfWeek.forEach { dayOfWeek ->
                    Text(
                        text = dayOfWeek.name.take(3),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(48.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        dayContent = { day: CalendarDay ->
            val isCurrentMonth = day.position == DayPosition.MonthDate
            val hasEvent = isCurrentMonth && events.any { it.date == day.date }
            val isSelected = selectedDate == day.date
            val baseTextColor = if (isCurrentMonth) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            }
            val borderColor = if (isCurrentMonth) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            }
            val backgroundColor = when {
                isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                isCurrentMonth -> MaterialTheme.colorScheme.surface
                else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            }
            val textColor = if (isSelected) MaterialTheme.colorScheme.primary else baseTextColor

            Column(
                modifier = Modifier
                    .width(48.dp)
                    .clickable(
                        enabled = isCurrentMonth,
                        onClick = { onDayClick(day.date) }
                    )
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = backgroundColor,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (hasEvent) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
            }
        }
    )
}
