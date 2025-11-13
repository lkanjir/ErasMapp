package com.rampu.erasmapp.schedule

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.rampu.erasmapp.schedule.data.FirestoreScheduleRepository
import com.rampu.erasmapp.schedule.data.ScheduleRepository
import com.rampu.erasmapp.schedule.ui.ScheduleViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val scheduleModule = module {
    single { Firebase.firestore }
    single<ScheduleRepository> { FirestoreScheduleRepository(get(), get()) }

    viewModel { ScheduleViewModel(get()) }
}
