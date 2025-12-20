package com.rampu.erasmapp.schedule.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rampu.erasmapp.schedule.domain.ScheduleEvent
import com.rampu.erasmapp.schedule.data.ScheduleRepository
import com.rampu.erasmapp.schedule.data.ScheduleSyncState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID

data class ScheduleUiState(
    val events: List<ScheduleEvent> = emptyList(),
    val isSyncing: Boolean = true,
    val syncErrorMessage: String? = null,
    val isSignedOut: Boolean = false,
    val isSaving: Boolean = false,
    val transientMessage: String? = null,
    val showAddEventDialog: Boolean = false,
    val newTitle: String = "",
    val newDateText: String = LocalDate.now().toString(),
    val newDate: LocalDate = LocalDate.now(),
    val newStart: String = "",
    val newEnd: String = "",
    val newLocation: String = "",
    val newCategory: String = "",
    val newIsEveryWeek: Boolean = false,
    val selectedEvent: ScheduleEvent? = null,
    val editTitle: String = "",
    val editDateText: String = "",
    val editStart: String = "",
    val editEnd: String = "",
    val editLocation: String = "",
    val editCategory: String = "",
    val editIsEveryWeek: Boolean = false,
    val currentDay: LocalDate = LocalDate.now(),
    val currentWeekStart: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY),
    val isWeeklyView: Boolean = true
)

class ScheduleViewModel(
    private val repository: ScheduleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState = _uiState.asStateFlow()
    private var observeJob: Job? = null

    init {
        observeEvents()
    }

    private fun observeEvents() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            repository.observeEvents().collect { syncState ->
                when (syncState) {
                    ScheduleSyncState.Loading -> _uiState.update {
                        it.copy(isSyncing = true, syncErrorMessage = null)
                    }

                    is ScheduleSyncState.Success -> _uiState.update {
                        it.copy(
                            events = syncState.events,
                            isSyncing = false,
                            syncErrorMessage = null,
                            isSignedOut = false
                        )
                    }

                    is ScheduleSyncState.Error -> _uiState.update {
                        it.copy(
                            isSyncing = false,
                            syncErrorMessage = syncState.message,
                            isSignedOut = false
                        )
                    }

                    ScheduleSyncState.SignedOut -> _uiState.update {
                        it.copy(
                            events = emptyList(),
                            isSyncing = false,
                            isSignedOut = true,
                            syncErrorMessage = "Sign in to view your schedule."
                        )
                    }
                }
            }
        }
    }

    fun refreshEvents() {
        observeEvents()
    }

    fun setAddDialogVisible(show: Boolean) {
        _uiState.update { it.copy(showAddEventDialog = show) }
    }

    fun updateNewTitle(value: String) = _uiState.update { it.copy(newTitle = value) }
    fun updateNewStart(value: String) = _uiState.update { it.copy(newStart = value) }
    fun updateNewEnd(value: String) = _uiState.update { it.copy(newEnd = value) }
    fun updateNewLocation(value: String) = _uiState.update { it.copy(newLocation = value) }
    fun updateNewCategory(value: String) = _uiState.update { it.copy(newCategory = value) }
    fun updateNewIsEveryWeek(value: Boolean) = _uiState.update { it.copy(newIsEveryWeek = value) }

    fun updateNewDate(input: String) {
        _uiState.update { state ->
            val parsedDate = runCatching { LocalDate.parse(input) }.getOrElse { state.newDate }
            state.copy(
                newDateText = input,
                newDate = parsedDate
            )
        }
    }

    fun saveNewEvent() {
        val state = _uiState.value
        if (state.newTitle.isBlank() || state.newStart.isBlank() || state.newEnd.isBlank()) {
            _uiState.update {
                it.copy(transientMessage = "Title, start time and end time are required.")
            }
            return
        }

        val newEvent = ScheduleEvent(
            id = UUID.randomUUID().toString(),
            title = state.newTitle,
            date = state.newDate,
            startTime = state.newStart,
            endTime = state.newEnd,
            location = state.newLocation.ifBlank { "-" },
            category = state.newCategory.ifBlank { "Other" },
            isEveryWeek = state.newIsEveryWeek
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

    fun selectEvent(event: ScheduleEvent) {
        _uiState.update {
            it.copy(
                selectedEvent = event,
                editTitle = event.title,
                editDateText = event.date.toString(),
                editStart = event.startTime,
                editEnd = event.endTime,
                editLocation = event.location,
                editCategory = event.category,
                editIsEveryWeek = event.isEveryWeek
            )
        }
    }

    fun dismissSelectedEvent() {
        _uiState.update { it.copy(selectedEvent = null) }
    }

    fun updateEditTitle(value: String) = _uiState.update { it.copy(editTitle = value) }
    fun updateEditDate(value: String) = _uiState.update { it.copy(editDateText = value) }
    fun updateEditStart(value: String) = _uiState.update { it.copy(editStart = value) }
    fun updateEditEnd(value: String) = _uiState.update { it.copy(editEnd = value) }
    fun updateEditLocation(value: String) = _uiState.update { it.copy(editLocation = value) }
    fun updateEditCategory(value: String) = _uiState.update { it.copy(editCategory = value) }
    fun updateEditIsEveryWeek(value: Boolean) = _uiState.update { it.copy(editIsEveryWeek = value) }

    fun saveEditedEvent() {
        val state = _uiState.value
        val selected = state.selectedEvent ?: return
        val parsedDate = runCatching { LocalDate.parse(state.editDateText) }.getOrNull()
        if (parsedDate == null) {
            _uiState.update { it.copy(transientMessage = "Date must be in YYYY-MM-DD format.") }
            return
        }

        val updatedEvent = selected.copy(
            title = state.editTitle,
            date = parsedDate,
            startTime = state.editStart,
            endTime = state.editEnd,
            location = state.editLocation,
            category = state.editCategory,
            isEveryWeek = state.editIsEveryWeek
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = repository.updateEvent(updatedEvent)
            _uiState.update {
                if (result.isSuccess) {
                    it.copy(
                        selectedEvent = null,
                        isSaving = false,
                        transientMessage = "Event updated"
                    )
                } else {
                    it.copy(
                        isSaving = false,
                        transientMessage = result.exceptionOrNull()?.localizedMessage
                            ?: "Unable to update event. Try again."
                    )
                }
            }
        }
    }

    fun toggleViewMode() {
        _uiState.update { it.copy(isWeeklyView = !it.isWeeklyView) }
    }

    fun previousWeek() {
        _uiState.update { it.copy(currentWeekStart = it.currentWeekStart.minusWeeks(1)) }
    }

    fun nextWeek() {
        _uiState.update { it.copy(currentWeekStart = it.currentWeekStart.plusWeeks(1)) }
    }

    fun previousDay() {
        _uiState.update { it.copy(currentDay = it.currentDay.minusDays(1)) }
    }

    fun nextDay() {
        _uiState.update { it.copy(currentDay = it.currentDay.plusDays(1)) }
    }

    fun deleteSelectedEvent() {
        val eventId = _uiState.value.selectedEvent?.id ?: return
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

    fun clearTransientMessage() {
        _uiState.update { it.copy(transientMessage = null) }
    }

    private fun ScheduleUiState.resetNewEventForm(): ScheduleUiState {
        val resetDate = LocalDate.now()
        return copy(
            showAddEventDialog = false,
            newTitle = "",
            newStart = "",
            newEnd = "",
            newDate = resetDate,
            newDateText = resetDate.toString(),
            newLocation = "",
            newCategory = "",
            newIsEveryWeek = false,
            isSaving = false,
            transientMessage = "Event saved"
        )
    }
}
