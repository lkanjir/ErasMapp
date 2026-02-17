package com.rampu.erasmapp.auth.domain

import kotlinx.serialization.Serializable

@Serializable
data class UserAccount (
    val uid: String  = "",
    val email: String? = null,
    val name: String? = null
)