package com.rampu.erasmapp.common.util

import android.content.Context
import android.icu.util.Calendar
import android.text.format.DateUtils

fun formatTime(context: Context, lastActivityAt: Long?): String {
    if (lastActivityAt == null) return "New"

    val now = System.currentTimeMillis()
    val cal = Calendar.getInstance().apply {
        timeInMillis = now
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val startOfToday = cal.timeInMillis
    val startOfYesterday = startOfToday - DateUtils.DAY_IN_MILLIS

    return when {
        lastActivityAt >= startOfToday -> DateUtils.formatDateTime(
            context,
            lastActivityAt,
            DateUtils.FORMAT_SHOW_TIME
        )

        lastActivityAt >= startOfYesterday -> "Yesterday"
        else -> DateUtils.formatDateTime(
            context,
            lastActivityAt,
            DateUtils.FORMAT_NUMERIC_DATE or DateUtils.FORMAT_NUMERIC_DATE
        )
    }
}