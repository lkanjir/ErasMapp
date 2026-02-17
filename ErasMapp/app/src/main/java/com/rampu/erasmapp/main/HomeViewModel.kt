package com.rampu.erasmapp.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rampu.erasmapp.channels.domian.Channel
import com.rampu.erasmapp.eventCalendar.domain.CalendarEvent
import com.rampu.erasmapp.main.data.HomeChannelsState
import com.rampu.erasmapp.main.data.HomeEventCalendarState
import com.rampu.erasmapp.main.data.HomeRepository
import com.rampu.erasmapp.main.data.HomeScheduleState
import com.rampu.erasmapp.schedule.domain.ScheduleEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isScheduleLoading: Boolean = true,
    val scheduleErrorMessage: String? = null,
    val isSignedOut: Boolean = false,
    val todaySchedule: List<ScheduleEvent> = emptyList(),
    val isChannelsLoading: Boolean = true,
    val channelsErrorMessage: String? = null,
    val channels: List<Channel> = emptyList(),
    val isChannelsSignedOut: Boolean = false,
    val isEventCalendarLoading: Boolean = true,
    val eventCalendarErrorMessage: String? = null,
    val upcomingEvents: List<CalendarEvent> = emptyList(),
    val isEventCalendarSignedOut: Boolean = false,
)

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    private var observeJob: Job? = null
    private var observeChannelsJob: Job? = null
    private var observeEventCalendarJob: Job? = null

    init {
        observeTodaySchedule()
        observeChannels()
        observeUpcomingEvents()
    }

    private fun observeTodaySchedule() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            repository.observeTodaySchedule().collect { state ->
                when (state) {
                    HomeScheduleState.Loading -> _uiState.update {
                        it.copy(
                            isScheduleLoading = true,
                            scheduleErrorMessage = null,
                            isSignedOut = false
                        )
                    }

                    is HomeScheduleState.Success -> _uiState.update {
                        it.copy(
                            isScheduleLoading = false,
                            scheduleErrorMessage = null,
                            isSignedOut = false,
                            todaySchedule = state.events
                        )
                    }

                    is HomeScheduleState.Error -> _uiState.update {
                        it.copy(
                            isScheduleLoading = false,
                            scheduleErrorMessage = state.message,
                            isSignedOut = false,
                            todaySchedule = emptyList()
                        )
                    }

                    HomeScheduleState.SignOut -> _uiState.update {
                        it.copy(
                            isScheduleLoading = false,
                            scheduleErrorMessage = "Sign in to view your schedule.",
                            isSignedOut = true,
                            todaySchedule = emptyList()
                        )
                    }
                }
            }
        }
    }

    private fun observeChannels() {
        observeChannelsJob?.cancel()
        observeChannelsJob = viewModelScope.launch {
            repository.observeChannels().collect { state ->
                when (state) {
                    HomeChannelsState.Loading -> _uiState.update {
                        it.copy(
                            isChannelsLoading = true,
                            channelsErrorMessage = null,
                            isChannelsSignedOut = false
                        )
                    }

                    is HomeChannelsState.Success -> _uiState.update {
                        it.copy(
                            isChannelsLoading = false,
                            channelsErrorMessage = null,
                            isChannelsSignedOut = false,
                            channels = state.channels
                        )
                    }

                    is HomeChannelsState.Error -> _uiState.update {
                        it.copy(
                            isChannelsLoading = false,
                            channelsErrorMessage = state.message,
                            isChannelsSignedOut = false,
                            channels = emptyList()
                        )
                    }

                    HomeChannelsState.SignOut -> _uiState.update {
                        it.copy(
                            isChannelsLoading = false,
                            channelsErrorMessage = "Sign in to view channels.",
                            isChannelsSignedOut = true,
                            channels = emptyList()
                        )
                    }
                }
            }
        }
    }

    private fun observeUpcomingEvents() {
        observeEventCalendarJob?.cancel()
        observeEventCalendarJob = viewModelScope.launch {
            repository.observeUpcomingEvents().collect { state ->
                when (state) {
                    HomeEventCalendarState.Loading -> _uiState.update {
                        it.copy(
                            isEventCalendarLoading = true,
                            eventCalendarErrorMessage = null,
                            isEventCalendarSignedOut = false
                        )
                    }

                    is HomeEventCalendarState.Success -> _uiState.update {
                        it.copy(
                            isEventCalendarLoading = false,
                            eventCalendarErrorMessage = null,
                            isEventCalendarSignedOut = false,
                            upcomingEvents = state.events
                        )
                    }

                    is HomeEventCalendarState.Error -> _uiState.update {
                        it.copy(
                            isEventCalendarLoading = false,
                            eventCalendarErrorMessage = state.message,
                            isEventCalendarSignedOut = false,
                            upcomingEvents = emptyList()
                        )
                    }

                    HomeEventCalendarState.SignOut -> _uiState.update {
                        it.copy(
                            isEventCalendarLoading = false,
                            eventCalendarErrorMessage = "Sign in to view events.",
                            isEventCalendarSignedOut = true,
                            upcomingEvents = emptyList()
                        )
                    }
                }
            }
        }
    }

    fun refreshTodaySchedule() {
        observeTodaySchedule()
    }
}