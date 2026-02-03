package com.rampu.erasmapp.main.data

import com.rampu.erasmapp.schedule.domain.ScheduleEvent
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HomeRepository {
    fun observeTodaySchedule(): Flow<HomeScheduleState>
}

sealed interface  HomeScheduleState{
    data object  Loading: HomeScheduleState
    data class  Success(val events:List<ScheduleEvent>): HomeScheduleState
    data class  Error(val message:String, val throwable: Throwable?=null): HomeScheduleState
    data object SignOut: HomeScheduleState
}