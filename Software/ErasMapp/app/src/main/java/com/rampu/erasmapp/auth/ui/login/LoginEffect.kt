package com.rampu.erasmapp.auth.ui.login

sealed interface LoginEffect {
    data object NavigateHome: LoginEffect
    data class ShowError(val msg: String) : LoginEffect
}