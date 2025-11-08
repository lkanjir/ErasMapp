package com.rampu.erasmapp.auth.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rampu.erasmapp.auth.domain.AuthResult
import com.rampu.erasmapp.auth.domain.IAuthRepository
import com.rampu.erasmapp.auth.validators.RegisterValidators
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel (private val repo: IAuthRepository) : ViewModel(){

    var uiState = MutableStateFlow(RegisterUiState())
        private set
    var effect = MutableSharedFlow<RegisterEffect>()
        private set

    fun onEvent(event: RegisterEvent){
        when(event){
            is RegisterEvent.EmailChanged -> uiState.update { it.copy(email = event.v) }
            is RegisterEvent.PasswordChanged -> uiState.update { it.copy(password = event.v) }
            is RegisterEvent.ConfirmPasswordChanged ->  uiState.update { it.copy(confirmPassword = event.v) }
            RegisterEvent.Submit -> register()
        }
    }

    fun register() = viewModelScope.launch {
        val email = uiState.value.email
        val password = uiState.value.password
        val confirmedPassword = uiState.value.confirmPassword

        val emailError = RegisterValidators.validateEmail(email)
        val passwordError = RegisterValidators.validatePassword(password)
        val confirmedPasswordError = RegisterValidators.validateConfirmPassword(password, confirmedPassword)

        if(emailError != null || passwordError != null || confirmedPasswordError != null){
            uiState.update { it.copy(
                emailError = emailError?.msg,
                passwordError = passwordError?.msg,
                confirmPasswordError = confirmedPasswordError?.msg)}

            return@launch
        }

        uiState.update { it.copy(isLoading = true) }
        when(val result = repo.register(email = email, password = password)){
            is AuthResult.Success -> {
                uiState.update { it.copy(isLoading = false) }
                effect.emit(RegisterEffect.NavigateHome)
            }
            is AuthResult.Failure -> {
                uiState.update { it.copy(isLoading = false) }
                effect.emit(RegisterEffect.ShowError(result.message ?: "Unknown error"))
            }
        }
    }

}