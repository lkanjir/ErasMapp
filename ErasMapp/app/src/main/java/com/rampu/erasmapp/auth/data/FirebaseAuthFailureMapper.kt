package com.rampu.erasmapp.auth.data

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.rampu.erasmapp.auth.domain.FailureReason

object FirebaseAuthFailureMapper {
    fun mapToFailureReason(e: Exception): FailureReason = when(e){
        is FirebaseNetworkException -> FailureReason.NETWORK
        is FirebaseAuthException -> when (e.errorCode) {
            "ERROR_INVALID_EMAIL" -> FailureReason.INVALID_EMAIL_FORMAT
            "ERROR_USER_NOT_FOUND" -> FailureReason.USER_NOT_FOUND
            "ERROR_WRONG_PASSWORD" -> FailureReason.INVALID_CREDENTIALS
            "ERROR_EMAIL_ALREADY_IN_USE" -> FailureReason.EMAIL_IN_USE
            "ERROR_WEAK_PASSWORD" -> FailureReason.WEAK_PASSWORD
            "ERROR_TOO_MANY_REQUESTS" -> FailureReason.RATE_LIMITED
            else -> FailureReason.OTHER
        }
        else -> FailureReason.OTHER
    }

    fun niceMessage(reason: FailureReason): String = when(reason){
        FailureReason.NETWORK -> "Network error. Check your connection."
        FailureReason.INVALID_EMAIL_FORMAT -> "Email format is invalid."
        FailureReason.USER_NOT_FOUND -> "No account exists with this email."
        FailureReason.INVALID_CREDENTIALS -> "Incorrect email or password."
        FailureReason.EMAIL_IN_USE -> "Email already in use"
        FailureReason.WEAK_PASSWORD -> "Password is too weak."
        FailureReason.RATE_LIMITED -> "Too many attempts. Try again later."
        FailureReason.OTHER -> "Something went wrong. Please try again."
    }
}