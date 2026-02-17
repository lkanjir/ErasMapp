package com.rampu.erasmapp.auth.domain

sealed class AuthResult {
    data class Success(val user: UserAccount) : AuthResult()
    data class Failure(val reason: FailureReason, val message: String? = null) : AuthResult()
}

enum class FailureReason {
    INVALID_CREDENTIALS,
    USER_NOT_FOUND,
    EMAIL_IN_USE,
    WEAK_PASSWORD,
    RATE_LIMITED,
    INVALID_EMAIL_FORMAT,
    NETWORK,
    OTHER
}