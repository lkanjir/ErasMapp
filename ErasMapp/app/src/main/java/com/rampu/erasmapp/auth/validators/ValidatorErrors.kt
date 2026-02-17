package com.rampu.erasmapp.auth.validators

sealed class ValidatorErrors (val msg: String){
    data object Empty : ValidatorErrors("Field is required")
    data object InvalidEmail : ValidatorErrors("Email format is invalid.")
    data object WeakPassword : ValidatorErrors("Password is too weak.")
    data object InvalidConfirmPassword : ValidatorErrors("Passwords do not match.")
}