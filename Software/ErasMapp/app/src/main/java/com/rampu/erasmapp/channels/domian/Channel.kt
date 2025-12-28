package com.rampu.erasmapp.channels.domian

data class Channel(
    val id: String,
    val title: String,
    val topic: String,
    val description: String?,
    val createdBy: String?,
    val iconKey: String?,
)