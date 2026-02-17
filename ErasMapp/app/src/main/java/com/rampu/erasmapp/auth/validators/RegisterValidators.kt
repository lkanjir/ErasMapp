package com.rampu.erasmapp.auth.validators

import android.util.Patterns

object RegisterValidators {
    fun validateEmail(email: String): ValidatorErrors? {
        val trimmed = email.trim()
        if(trimmed.isEmpty()) return ValidatorErrors.Empty

        val isCorrectFormat = Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()
        if(!isCorrectFormat) return ValidatorErrors.InvalidEmail
        return null
    }

    private val passwordHasLetters = Regex("[A-Za-z]")
    private val passwordHasNumbers = Regex("\\d")

    fun validatePassword(password: String): ValidatorErrors? {
        if(password.isEmpty()) return ValidatorErrors.Empty
        else if(password.length < 8) return ValidatorErrors.WeakPassword
        else if (!passwordHasLetters.containsMatchIn(password) || !passwordHasNumbers.containsMatchIn(password))
            return ValidatorErrors.WeakPassword

        return null
    }

    fun validateConfirmPassword(password: String, confirmedPassword: String): ValidatorErrors? {
        if(confirmedPassword.isEmpty()) return ValidatorErrors.Empty
        if(password != confirmedPassword) return ValidatorErrors.InvalidConfirmPassword
        return null
    }
}