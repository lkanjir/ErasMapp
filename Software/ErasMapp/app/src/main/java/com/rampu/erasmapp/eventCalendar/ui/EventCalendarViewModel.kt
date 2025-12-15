package com.rampu.erasmapp.eventCalendar.ui

import androidx.lifecycle.ViewModel
import com.rampu.erasmapp.eventCalendar.domain.CalendarEvent
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EventCalendarUiState(
    val events: List<CalendarEvent> = emptyList(),
    val selectedDate: LocalDate? = null
)

class EventCalendarViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EventCalendarUiState())
    val uiState = _uiState.asStateFlow()

    init {
        seedEvents()
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    private fun seedEvents() {
        val today = LocalDate.now()
        val presetEvents = listOf(
            CalendarEvent(
                date = today,
                title = "Campus Tour",
                time = "10:00 - 12:00",
                location = "FOI1",
                description = "Guided walk through faculty buildings and student services."
            ),
            CalendarEvent(
                date = today.plusDays(2),
                title = "Erasmus Meetup",
                time = "18:00 - 20:00",
                location = "Student Center",
                description = "Casual networking for exchange students with snacks and music."
            ),
            CalendarEvent(
                date = today.plusWeeks(1),
                title = "Project Prep2",
                time = "14:00 - 16:30",
                location = "FOI2",
                description = "Hands-on session to kick off team projects and set milestones."
            ),
            CalendarEvent(
                date = today.plusWeeks(1),
                title = "City Walk Through",
                time = "17:00 - 19:00",
                location = "Student Center",
                description = "Learning the city of Varaždin."
            ),
            CalendarEvent(
                date = today.minusDays(1),
                title = "Project Prep1",
                time = "09:00 - 11:00",
                location = "FOI1",
                description = "Hands-on session to kick off team projects and set milestones"
            )
        )

        _uiState.update {
            it.copy(
                events = presetEvents,
                selectedDate = presetEvents.firstOrNull()?.date
            )
        }
    }
}
