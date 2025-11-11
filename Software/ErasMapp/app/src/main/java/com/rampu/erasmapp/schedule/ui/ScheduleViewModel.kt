package com.rampu.erasmapp.schedule.ui

import androidx.lifecycle.ViewModel
import com.rampu.erasmapp.schedule.data.ScheduleEvent
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ScheduleUiState(
    val events: List<ScheduleEvent> = demoEvents(),
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

class ScheduleViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState = _uiState.asStateFlow()

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
            return
        }

        val updatedEvents = state.events.toMutableList().apply {
            add(
                ScheduleEvent(
                    id = UUID.randomUUID().toString(),
                    title = state.newTitle,
                    date = state.newDate,
                    startTime = state.newStart,
                    endTime = state.newEnd,
                    location = state.newLocation.ifBlank { "-" },
                    category = state.newCategory.ifBlank { "Other" },
                    isEveryWeek = state.newIsEveryWeek
                )
            )
        }

        val resetDate = LocalDate.now()
        _uiState.update {
            it.copy(
                events = updatedEvents,
                showAddEventDialog = false,
                newTitle = "",
                newStart = "",
                newEnd = "",
                newDate = resetDate,
                newDateText = resetDate.toString(),
                newLocation = "",
                newCategory = "",
                newIsEveryWeek = false
            )
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
        val parsedDate = runCatching { LocalDate.parse(state.editDateText) }.getOrNull() ?: return

        val updatedEvent = selected.copy(
            title = state.editTitle,
            date = parsedDate,
            startTime = state.editStart,
            endTime = state.editEnd,
            location = state.editLocation,
            category = state.editCategory,
            isEveryWeek = state.editIsEveryWeek
        )

        _uiState.update {
            val events = it.events.toMutableList()
            val index = events.indexOfFirst { event -> event.id == selected.id }
            if (index != -1) {
                events[index] = updatedEvent
            }
            it.copy(
                events = events,
                selectedEvent = null
            )
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
}

private fun demoEvents(): List<ScheduleEvent> {
    val monday = LocalDate.now().with(DayOfWeek.MONDAY)
    return mutableListOf(
        ScheduleEvent(
            "1",
            "RWA",
            monday,
            "10:00",
            "12:00",
            location = "Room 3, FOI1",
            category = "Lecture",
            isEveryWeek = true
        ),
        ScheduleEvent("2", "RAMPU", monday, "12:00", "15:00", location = "Room 3, FOI1", category = "Lecture", isEveryWeek = true),
        ScheduleEvent("3", "RPP", monday.plusDays(1), "8:00", "10:00", location = "Room 3, FOI2", category = "Lecture", isEveryWeek = true),
        ScheduleEvent("4", "RAMPU", monday.plusDays(1), "12:00", "18:00", location = "-", category = "Other", isEveryWeek = false),
        ScheduleEvent("5", "POP", monday.plusDays(2), "12:00", "14:00", location = "Room 7, FOI1", category = "Lecture", isEveryWeek = true),
        ScheduleEvent("6", "POP", monday.plusDays(2), "12:00", "14:00", location = "Room 7, FOI1", category = "Mid-Term", isEveryWeek = false),
        ScheduleEvent("7", "RPP", monday.plusDays(3), "17:00", "18:30", location = "Room 4, FOI1", category = "Exercises", isEveryWeek = true)
    )
}
