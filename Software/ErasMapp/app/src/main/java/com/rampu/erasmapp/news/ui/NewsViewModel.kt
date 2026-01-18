package com.rampu.erasmapp.news.ui

import androidx.lifecycle.ViewModel
import com.rampu.erasmapp.news.domain.INewsRepository
import com.rampu.erasmapp.user.domain.IUserRepository
import kotlinx.coroutines.flow.MutableStateFlow

class NewsViewModel(private val repo: INewsRepository, private val userRepo: IUserRepository) :
    ViewModel() {

    var uiState = MutableStateFlow(NewsUiState())
        private set

    fun onEvent(event: NewsEvent){

    }
}
