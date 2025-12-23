package com.rampu.erasmapp.channels.ui

sealed interface ChannelEvent {
    data class TitleChanged(val v: String) : ChannelEvent
    data class TopicChanged(val v: String) : ChannelEvent
    data class DescriptionChanged(val v: String) : ChannelEvent
    data object CreateChannel: ChannelEvent
    data class ShowCreateDialog(val show: Boolean): ChannelEvent
}