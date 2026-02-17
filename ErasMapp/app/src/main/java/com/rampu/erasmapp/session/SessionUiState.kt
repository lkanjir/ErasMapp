package com.rampu.erasmapp.session

import com.rampu.erasmapp.auth.domain.UserAccount

data class SessionUiState(
    val user: UserAccount? = null,
    val isLoadingUserStatus: Boolean = true
)