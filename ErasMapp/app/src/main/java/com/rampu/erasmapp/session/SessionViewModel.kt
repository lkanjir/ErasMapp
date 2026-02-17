package com.rampu.erasmapp.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rampu.erasmapp.auth.domain.IAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SessionViewModel(private val repo : IAuthRepository) : ViewModel(){

    var state = MutableStateFlow(SessionUiState())
        private set

    init{
        viewModelScope.launch {
            repo.authState.collect { account ->
                state.update { it.copy(user = account, isLoadingUserStatus = false) }
            }
        }
    }

    fun signOut() = viewModelScope.launch {
        repo.signOut()
    }
}