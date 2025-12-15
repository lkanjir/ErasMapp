package com.rampu.erasmapp.eventCalendar.domain

import java.time.LocalDate

data class CalendarEvent(
    val date: LocalDate,
    val title: String,
    val time: String,
    val location: String,
    val description: String
)

