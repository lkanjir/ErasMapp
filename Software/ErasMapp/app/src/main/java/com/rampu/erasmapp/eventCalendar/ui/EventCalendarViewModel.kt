package com.rampu.erasmapp.eventCalendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rampu.erasmapp.eventCalendar.data.EventCalendarRepository
import com.rampu.erasmapp.eventCalendar.data.EventCalendarSyncState
import com.rampu.erasmapp.eventCalendar.domain.CalendarEvent
import java.time.LocalDate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventCalendarUiState(
    val events: List<CalendarEvent> = emptyList(),
    val selectedDate: LocalDate? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isSignedOut: Boolean = false
)

class EventCalendarViewModel(
    private val repository: EventCalendarRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EventCalendarUiState())
    val uiState = _uiState.asStateFlow()
    private var observeJob: Job? = null

    init {
        observeEvents()
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun refreshEvents() {
        observeEvents()
    }

    private fun observeEvents() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            repository.observeEvents().collect { syncState ->
                when (syncState) {
                    EventCalendarSyncState.Loading -> _uiState.update {
                        it.copy(isLoading = true, errorMessage = null)
                    }

                    is EventCalendarSyncState.Success -> _uiState.update { state ->
                        val newSelectedDate = state.selectedDate
                            ?.takeIf { selected -> syncState.events.any { it.date == selected } }

                        state.copy(
                            events = syncState.events,
                            selectedDate = newSelectedDate,
                            isLoading = false,
                            errorMessage = null,
                            isSignedOut = false
                        )
                    }

                    is EventCalendarSyncState.Error -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = syncState.message,
                            isSignedOut = false
                        )
                    }

                    EventCalendarSyncState.SignedOut -> _uiState.update {
                        it.copy(
                            events = emptyList(),
                            selectedDate = null,
                            isLoading = false,
                            errorMessage = "Sign in to view events.",
                            isSignedOut = true
                        )
                    }
                }
            }
        }
    }
}
