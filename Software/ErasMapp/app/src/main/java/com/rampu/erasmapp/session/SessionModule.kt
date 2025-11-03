package com.rampu.erasmapp.session

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val sessionModule = module{
    viewModel{
        SessionViewModel(get())
    }
}