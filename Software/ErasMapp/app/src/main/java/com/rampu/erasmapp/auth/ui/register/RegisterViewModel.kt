package com.rampu.erasmapp.auth.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rampu.erasmapp.auth.domain.AuthResult
import com.rampu.erasmapp.auth.domain.IAuthRepository
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
            is RegisterEvent.NameChanged -> uiState.update { it.copy(name = event.v, error = null) }
            is RegisterEvent.EmailChanged -> uiState.update { it.copy(email = event.v, error = null) }
            is RegisterEvent.PasswordChanged -> uiState.update { it.copy(password = event.v, error = null) }
            RegisterEvent.Submit -> register()
        }
    }

    fun register() = viewModelScope.launch {
        val name = uiState.value.name
        val email = uiState.value.email
        val password = uiState.value.password

        uiState.update { it.copy(isLoading = true, error = null) }
        when(val result = repo.register(email = email, password = password, name = name)){
            is AuthResult.Success -> {
                uiState.update { it.copy(isLoading = false) }
                effect.emit(RegisterEffect.NavigateHome)
            }
            is AuthResult.Failure -> {
                uiState.update { it.copy(isLoading = false, error = result.message) }
                effect.emit(RegisterEffect.ShowError(result.message ?: "Unknown error"))
            }
        }
    }

}