package com.rampu.erasmapp.channels.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rampu.erasmapp.channels.domian.ChannelSyncState
import com.rampu.erasmapp.channels.domian.IChannelRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChannelsViewModel(private val repo: IChannelRepository) : ViewModel() {
    var uiState = MutableStateFlow(ChannelUiState())
        private set;
    private var observeJob: Job? = null;

    init {
        observerChannels()
    }

    private fun observerChannels() {
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
                createChennel()
                uiState.update { it.copy(showCreateDialog = false)}
                }
            is ChannelEvent.ShowCreateDialog -> uiState.update { it.copy(showCreateDialog = event.show) }
        }
    }

    private fun createChennel() {
        viewModelScope.launch {
            val result = repo.createChannel(
                title = uiState.value.newTitle,
                topic = uiState.value.newTopic,
                description = uiState.value.newDescription
            )
            uiState.update {
                if (result.isSuccess) {
                    it.copy(
                        newTitle = "",
                        newTopic = "",
                        newDescription = ""
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