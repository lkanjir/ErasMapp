package com.rampu.erasmapp.auth.ui.register

sealed interface RegisterEffect {
    data object NavigateHome : RegisterEffect
    data class ShowError(val msg: String) : RegisterEffect
}


