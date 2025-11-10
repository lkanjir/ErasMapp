package com.rampu.erasmapp.schedule.data

import java.time.LocalDate
data class ScheduleEvent(
    val id: String,
    val title: String,
    val date: LocalDate,
    val startTime: String,
    val endTime: String
)
