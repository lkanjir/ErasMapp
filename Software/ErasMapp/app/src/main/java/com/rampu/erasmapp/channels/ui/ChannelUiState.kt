package com.rampu.erasmapp.channels.ui

import com.rampu.erasmapp.channels.domian.Channel

data class ChannelUiState(
    val channels: List<Channel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    val isSignedOut: Boolean = false,
    val showCreateDialog: Boolean = false,
    val newTitle: String = "",
    val newTopic : String = "",
    val newDescription: String = "",
)