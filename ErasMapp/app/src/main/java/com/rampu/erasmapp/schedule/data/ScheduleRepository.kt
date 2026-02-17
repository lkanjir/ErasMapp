package com.rampu.erasmapp.schedule.data

import com.rampu.erasmapp.schedule.domain.ScheduleEvent
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun observeEvents(): Flow<ScheduleSyncState>
    suspend fun createEvent(event: ScheduleEvent): Result<Unit>
    suspend fun updateEvent(event: ScheduleEvent): Result<Unit>
    suspend fun deleteEvent(eventId: String): Result<Unit>
}

sealed interface ScheduleSyncState {
    data object Loading : ScheduleSyncState
    data class Success(val events: List<ScheduleEvent>) : ScheduleSyncState
    data class Error(val message: String, val throwable: Throwable? = null) : ScheduleSyncState
    data object SignedOut : ScheduleSyncState
}
