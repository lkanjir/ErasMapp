package com.rampu.erasmapp.auth

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.rampu.erasmapp.auth.data.FirebaseAuthImplementation
import com.rampu.erasmapp.auth.domain.IAuthRepository
import com.rampu.erasmapp.auth.ui.login.LoginViewModel
import com.rampu.erasmapp.auth.ui.register.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module{
    single { Firebase.auth}
    single<IAuthRepository> { FirebaseAuthImplementation(get()) }

    viewModel {
        LoginViewModel(get())
    }

    viewModel {
        RegisterViewModel(get())
    }
}