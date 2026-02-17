package com.rampu.erasmapp.auth.validators

import android.util.Patterns

object LoginValidators {
    fun validateEmail(email: String): ValidatorErrors? {
        val trimmed = email.trim()
        if(trimmed.isEmpty()) return ValidatorErrors.Empty

        val isCorrectFormat = Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()
        if(!isCorrectFormat) return ValidatorErrors.InvalidEmail
        return null
    }

    fun validatePassword(password: String): ValidatorErrors? {
        if(password.isEmpty()) return ValidatorErrors.Empty
        return null
    }

}