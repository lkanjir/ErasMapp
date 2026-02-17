package com.rampu.erasmapp.channels.ui.channels

import androidx.compose.runtime.currentComposer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rampu.erasmapp.channels.domian.ChannelSyncState
import com.rampu.erasmapp.channels.domian.IChannelRepository
import com.rampu.erasmapp.user.domain.IUserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChannelsViewModel(
    private val repo: IChannelRepository,
    private val userRepo: IUserRepository
) : ViewModel() {
    var uiState = MutableStateFlow(ChannelUiState())
        private set;
    private var observeJob: Job? = null
    private var adminJob: Job? = null

    init {
        observeAdminStatus()
        observeChannels()
    }

    private fun observeAdminStatus() {
        adminJob?.cancel()
        adminJob = viewModelScope.launch {
            userRepo.observeAdminStatus().collect { isAdmin ->
                uiState.update {
                    it.copy(
                        isAdmin = isAdmin,
                        showCreateDialog = if (isAdmin) it.showCreateDialog else false
                    )
                }
            }
        }
    }

    private fun observeChannels() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            repo.observeChannels().collect { state ->
                when (state) {
                    is ChannelSyncState.Loading -> uiState.update {
                        it.copy(
                            isLoading = true, errorMsg = null
                        )
                    }

                    is ChannelSyncState.Success -> uiState.update {
                        it.copy(
                            channels = state.channels,
                            isLoading = false,
                            errorMsg = null,
                            isSignedOut = false
                        )
                    }

                    is ChannelSyncState.Error -> uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = state.message,
                            isSignedOut = false
                        )
                    }

                    is ChannelSyncState.SignedOut -> uiState.update {
                        it.copy(
                            channels = emptyList(),
                            isLoading = false,
                            errorMsg = "You need to sign in to view channels",
                            isSignedOut = true
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: ChannelEvent) {
        when (event) {
            is ChannelEvent.TitleChanged -> uiState.update { it.copy(newTitle = event.v) }
            is ChannelEvent.TopicChanged -> uiState.update { it.copy(newTopic = event.v) }
            is ChannelEvent.DescriptionChanged -> uiState.update { it.copy(newDescription = event.v) }
            is ChannelEvent.CreateChannel -> {
                if (!uiState.value.isAdmin) {
                    uiState.update { it.copy(errorMsg = "Only staff can create channels") }
                    return
                }
                createChannel()
                uiState.update { it.copy(showCreateDialog = false) }
            }

            is ChannelEvent.ShowCreateDialog -> {
                if (!uiState.value.isAdmin && event.show)
                    uiState.update { it.copy(errorMsg = "Only staff can create channels") }
                else uiState.update { it.copy(showCreateDialog = event.show) }
            }

            is ChannelEvent.IconChanged -> uiState.update { it.copy(newIconKey = event.key) }
        }
    }

    private fun createChannel() {
        if (!uiState.value.isAdmin) {
            uiState.update { it.copy(errorMsg = "Only staff can create channels") }
            return
        }

        viewModelScope.launch {
            val result = repo.createChannel(
                title = uiState.value.newTitle,
                topic = uiState.value.newTopic,
                description = uiState.value.newDescription,
                iconKey = uiState.value.newIconKey
            )
            uiState.update {
                if (result.isSuccess) {
                    it.copy(
                        newTitle = "",
                        newTopic = "",
                        newDescription = "",
                        newIconKey = null
                    )
                } else {
                    it.copy(
                        errorMsg = "Unable to create channel"
                    )
                }
            }
        }
    }
}