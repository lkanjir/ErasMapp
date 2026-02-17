package com.rampu.erasmapp.main.data

import com.rampu.erasmapp.channels.domian.Channel
import com.rampu.erasmapp.eventCalendar.domain.CalendarEvent
import com.rampu.erasmapp.schedule.domain.ScheduleEvent
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun observeTodaySchedule(): Flow<HomeScheduleState>
    fun observeChannels(): Flow<HomeChannelsState>
    fun observeUpcomingEvents(): Flow<HomeEventCalendarState>
}

sealed interface  HomeScheduleState{
    data object  Loading: HomeScheduleState
    data class  Success(val events:List<ScheduleEvent>): HomeScheduleState
    data class  Error(val message:String, val throwable: Throwable?=null): HomeScheduleState
    data object SignOut: HomeScheduleState
}

sealed interface HomeChannelsState {
    data object Loading : HomeChannelsState
    data class Success(val channels: List<Channel>) : HomeChannelsState
    data class Error(val message: String) : HomeChannelsState
    data object SignOut : HomeChannelsState
}

sealed interface HomeEventCalendarState {
    data object Loading : HomeEventCalendarState
    data class Success(val events: List<CalendarEvent>) : HomeEventCalendarState
    data class Error(val message: String, val throwable: Throwable? = null) : HomeEventCalendarState
    data object SignOut : HomeEventCalendarState
}
