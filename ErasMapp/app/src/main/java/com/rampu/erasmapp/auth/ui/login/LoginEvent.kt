package com.rampu.erasmapp.auth.ui.login

sealed interface  LoginEvent{
    data class EmailChanged(val v: String) : LoginEvent
    data class PasswordChanged(val v: String) : LoginEvent
    data object Submit: LoginEvent
}