package com.rampu.erasmapp.schedule.data

import java.time.LocalDate
data class ScheduleEvent(
    val id: String,
    var title: String,
    var date: LocalDate,
    var startTime: String,
    var endTime: String
)
