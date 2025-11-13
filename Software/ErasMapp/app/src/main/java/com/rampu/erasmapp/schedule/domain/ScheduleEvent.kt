package com.rampu.erasmapp.schedule.domain

import java.time.LocalDate

data class ScheduleEvent(
    val id: String,
    var title: String,
    var date: LocalDate,
    var startTime: String,
    var endTime: String,
    var location: String,
    var category: String,
    var isEveryWeek: Boolean
)