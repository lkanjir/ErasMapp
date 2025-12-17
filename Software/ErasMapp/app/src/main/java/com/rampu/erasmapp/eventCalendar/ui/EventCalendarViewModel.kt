package com.rampu.erasmapp.eventCalendar.ui

import android.R
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
import java.util.UUID

data class EventCalendarUiState(
    val events: List<CalendarEvent> = emptyList(),
    val selectedDate: LocalDate? = null,
    val showAddCalendarEventDialog: Boolean = false,
    val isSaving: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isSignedOut: Boolean = false,
    val isAdmin: Boolean = false,
    val newTitle: String = "",
    val newDateText: String = LocalDate.now().toString(),
    val newTime: String = "",
    val newLocation: String = "",
    val newDescription: String = "",
    val transientMessage: String? = null,
    val selectedEvent: CalendarEvent? = null,
)

class EventCalendarViewModel(
    private val repository: EventCalendarRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EventCalendarUiState())
    val uiState = _uiState.asStateFlow()
    private var observeJob: Job? = null
    private var adminObserveJob: Job? = null

    init {
        observeEvents()
        observeAdminStatus()
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

    private fun observeAdminStatus() {
        adminObserveJob?.cancel()
        adminObserveJob = viewModelScope.launch {
            repository.observeAdminStatus().collect { isAdmin ->
                _uiState.update { state ->
                    state.copy(
                        isAdmin = isAdmin,
                        showAddCalendarEventDialog = if (isAdmin) {
                            state.showAddCalendarEventDialog
                        } else {
                            false
                        }
                    )
                }
            }
        }
    }

    fun setAddDialogVisible(show: Boolean) {
        _uiState.update { it.copy(showAddCalendarEventDialog = show) }
    }

    fun updateNewTitle(value: String) = _uiState.update { it.copy(newTitle = value) }
    fun updateNewDateText(value: String) = _uiState.update { it.copy(newDateText = value) }
    fun updateNewTime(value: String) = _uiState.update { it.copy(newTime = value) }
    fun updateNewLocation(value: String) = _uiState.update { it.copy(newLocation = value) }
    fun updateNewDescription(value: String) = _uiState.update { it.copy(newDescription = value) }

    fun saveNewEvent() {
        val state = _uiState.value
        if (state.newTitle.isBlank() || state.newDateText.isBlank() || state.newTime.isBlank()) {
            _uiState.update {
                it.copy(transientMessage = "Title, date and time are required.")
            }
            return
        }

        val parsedDate = runCatching { LocalDate.parse(state.newDateText) }.getOrNull()
        if (parsedDate == null) {
            _uiState.update { it.copy(transientMessage = "Date must be in YYYY-MM-DD format.") }
            return
        }

        val newEvent = CalendarEvent(
            id = UUID.randomUUID().toString(),
            title = state.newTitle,
            date = parsedDate,
            time = state.newTime,
            location = state.newLocation,
            description = state.newDescription.ifBlank { "No description" }
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = repository.createEvent(newEvent)
            _uiState.update {
                if (result.isSuccess) {
                    it.resetNewEventForm()
                } else {
                    it.copy(
                        isSaving = false,
                        transientMessage = result.exceptionOrNull()?.localizedMessage
                            ?: "Unable to save event. Try again."
                    )
                }
            }
        }
    }

    fun deleteSelectedEvent(eventId: String) {
        if(eventId == ""){
            return;
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = repository.deleteEvent(eventId)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(
                        selectedEvent = null,
                        isSaving = false,
                        transientMessage = "Event deleted"
                    )
                } else {
                    it.copy(
                        isSaving = false,
                        transientMessage = result.exceptionOrNull()?.localizedMessage
                            ?: "Unable to delete event. Try again."
                    )
                }
            }
        }
    }

    private fun EventCalendarUiState.resetNewEventForm(): EventCalendarUiState {
        val resetDate = LocalDate.now()
        return copy(
            showAddCalendarEventDialog = false,
            newTitle = "",
            newDateText = resetDate.toString(),
            newTime = "",
            newLocation = "",
            newDescription = "",
            isSaving = false,
            transientMessage = "Event saved"
        )
    }
}
