package com.rampu.erasmapp.auth.ui.register

sealed interface RegisterEvent {
    data class NameChanged(val v: String): RegisterEvent
    data class EmailChanged(val v: String): RegisterEvent
    data class PasswordChanged(val v: String): RegisterEvent
    data object Submit: RegisterEvent
}