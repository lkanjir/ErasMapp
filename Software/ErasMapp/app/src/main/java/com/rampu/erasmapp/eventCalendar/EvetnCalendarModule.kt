package com.rampu.erasmapp.eventCalendar

import com.rampu.erasmapp.eventCalendar.ui.EventCalendarViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val eventCalendarModule = module {
    viewModel { EventCalendarViewModel() }
}
