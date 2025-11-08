package com.rampu.erasmapp.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rampu.erasmapp.auth.domain.AuthResult
import com.rampu.erasmapp.auth.domain.IAuthRepository
import com.rampu.erasmapp.auth.validators.LoginValidators
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel (private val repo: IAuthRepository) : ViewModel(){

    var uiState = MutableStateFlow(LoginUiState())
        private set
    var effect = MutableSharedFlow<LoginEffect>()
        private set

    fun onEvent(event: LoginEvent){
        when(event){
            is LoginEvent.EmailChanged -> uiState.update { it.copy(email = event.v) }
            is LoginEvent.PasswordChanged -> uiState.update { it.copy(password = event.v) }
            LoginEvent.Submit -> signIn()
        }
    }

    fun signIn() = viewModelScope.launch {
        val email = uiState.value.email
        val password = uiState.value.password

        val emailError = LoginValidators.validateEmail(email)
        val passwordError = LoginValidators.validatePassword(password)

        if(emailError != null || passwordError != null){
            uiState.update { it.copy(
                emailError = emailError?.msg,
                passwordError = passwordError?.msg)}

            return@launch
        }

        uiState.update { it.copy(isLoading = true) }
        when(val result = repo.signIn(email,password)){
            is AuthResult.Success -> {
                uiState.update { it.copy(isLoading = false) }
                effect.emit(LoginEffect.NavigateHome)
            }
            is AuthResult.Failure -> {
                uiState.update { it.copy(isLoading = false) }
                effect.emit(LoginEffect.ShowError(result.message ?: "Unknown error"))
            }
        }
    }

}