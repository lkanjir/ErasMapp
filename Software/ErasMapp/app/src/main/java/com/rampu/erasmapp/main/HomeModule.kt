package com.rampu.erasmapp.main

import com.rampu.erasmapp.main.data.HomeRepository
import com.rampu.erasmapp.main.data.HomeRepositoryImplementation
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    single<HomeRepository> { HomeRepositoryImplementation(get(), get()) }
    viewModel { HomeViewModel(get()) }
}