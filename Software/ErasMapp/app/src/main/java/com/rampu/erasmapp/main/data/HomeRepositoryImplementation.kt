package com.rampu.erasmapp.main.data

import com.rampu.erasmapp.channels.domian.ChannelSyncState
import com.rampu.erasmapp.channels.domian.IChannelRepository
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
    private val channelRepository: IChannelRepository
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
}