package com.rampu.erasmapp.main.data

import com.rampu.erasmapp.channels.domian.ChannelSyncState
import com.rampu.erasmapp.channels.domian.IChannelRepository
import com.rampu.erasmapp.eventCalendar.data.EventCalendarRepository
import com.rampu.erasmapp.eventCalendar.data.EventCalendarSyncState
import com.rampu.erasmapp.eventCalendar.domain.CalendarEvent
import com.rampu.erasmapp.schedule.data.ScheduleRepository
import com.rampu.erasmapp.schedule.data.ScheduleSyncState
import com.rampu.erasmapp.schedule.domain.ScheduleEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class HomeRepositoryImplementation(
    private val scheduleRepository: ScheduleRepository,
    private val channelRepository: IChannelRepository,
    private val eventCalendarRepository: EventCalendarRepository
) : HomeRepository {
    override fun observeTodaySchedule(): Flow<HomeScheduleState> {
        return scheduleRepository.observeEvents().map{ state ->
            when(state){
                ScheduleSyncState.Loading -> HomeScheduleState.Loading
                ScheduleSyncState.SignedOut -> HomeScheduleState.SignOut
                is ScheduleSyncState.Error -> HomeScheduleState.Error(state.message, state.throwable)
                is ScheduleSyncState.Success -> {
                    val today = LocalDate.now()
                    val todaysEvents = state.events
                        .filter{isForDate(it, today)}
                        .sortedWith(
                            compareBy<ScheduleEvent> { parseStartTime(it.startTime) ?: LocalTime.MAX }
                                .thenBy { it.title }
                        )
                    HomeScheduleState.Success(todaysEvents)
                }
            }
        }
    }

    override fun observeChannels(): Flow<HomeChannelsState> {
        return channelRepository.observeChannels().map { state ->
            when (state) {
                ChannelSyncState.Loading -> HomeChannelsState.Loading
                ChannelSyncState.SignedOut -> HomeChannelsState.SignOut
                is ChannelSyncState.Error -> HomeChannelsState.Error(state.message)
                is ChannelSyncState.Success -> HomeChannelsState.Success(state.channels)
            }
        }
    }

    override fun observeUpcomingEvents(): Flow<HomeEventCalendarState> {
        return eventCalendarRepository.observeEvents().map { state ->
            when (state) {
                EventCalendarSyncState.Loading -> HomeEventCalendarState.Loading
                EventCalendarSyncState.SignedOut -> HomeEventCalendarState.SignOut
                is EventCalendarSyncState.Error -> HomeEventCalendarState.Error(
                    state.message,
                    state.throwable
                )

                is EventCalendarSyncState.Success -> {
                    val today = LocalDate.now()
                    val upcoming = state.events
                        .filter { it.date >= today }
                        .sortedWith(
                            compareBy<CalendarEvent> { it.date }
                                .thenBy { parseEventTime(it.time) ?: LocalTime.MAX }
                                .thenBy { it.title }
                        )
                    HomeEventCalendarState.Success(upcoming)
                }
            }
        }
    }

    private fun isForDate(event: ScheduleEvent, date: LocalDate): Boolean{
        return  event.date == date || (event.isEveryWeek && event.date.dayOfWeek == date.dayOfWeek)
    }

    private fun parseStartTime(text:String): LocalTime?{
        val trimmed = text.trim()
        if(trimmed.isEmpty() ||trimmed == "-") return null
        val patterns = listOf("H:mm", "HH:mm")
        for (pattern in patterns) {
            val parsed = runCatching {
                LocalTime.parse(trimmed, DateTimeFormatter.ofPattern(pattern))
            }.getOrNull()
            if (parsed != null) return parsed
        }
        return null
    }

    private fun parseEventTime(timeText: String): LocalTime? {
        val trimmed = timeText.trim()
        if (trimmed.isEmpty() || trimmed == "-") {
            return null
        }

        val startTimeText = trimmed.split("-").firstOrNull()?.trim().orEmpty()
        if (startTimeText.isEmpty()) {
            return null
        }

        val patterns = listOf("H:mm", "HH:mm")
        for (pattern in patterns) {
            val parsed = runCatching {
                LocalTime.parse(startTimeText, DateTimeFormatter.ofPattern(pattern))
            }.getOrNull()
            if (parsed != null) return parsed
        }

        return null
    }
}