package com.rampu.erasmapp.common.util

//TODO: need to change when i add post registration flow
fun emailPrefix(email: String?): String =
    if (email.isNullOrBlank()) "unknown"
    else email.substringBefore("@")

fun initialFromLabel(label: String?): String{
    val trimmed = label?.trim().orEmpty()
    return trimmed.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
}