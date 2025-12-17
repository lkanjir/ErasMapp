package com.rampu.erasmapp.eventCalendar.data

import com.rampu.erasmapp.eventCalendar.domain.CalendarEvent
import kotlinx.coroutines.flow.Flow

interface EventCalendarRepository {
    fun observeEvents(): Flow<EventCalendarSyncState>
    fun observeAdminStatus(): Flow<Boolean>
    suspend fun createEvent(event: CalendarEvent): Result<Unit>
    suspend fun deleteEvent(eventId: String): Result<Unit>
}

sealed interface EventCalendarSyncState {
    data object Loading : EventCalendarSyncState
    data class Success(val events: List<CalendarEvent>) : EventCalendarSyncState
    data class Error(val message: String, val throwable: Throwable? = null) : EventCalendarSyncState
    data object SignedOut : EventCalendarSyncState
}
