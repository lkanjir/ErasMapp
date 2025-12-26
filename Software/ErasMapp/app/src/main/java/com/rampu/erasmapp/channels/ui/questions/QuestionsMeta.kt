package com.rampu.erasmapp.channels.ui.questions

import android.content.Context
import android.icu.util.Calendar
import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun QuestionsMeta(lastActivityAt: Long?, unreadCount: Long, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formatTime(context, lastActivityAt),
            style = MaterialTheme.typography.labelSmall
        )

        if (unreadCount > 0) {
            Spacer(Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(top = 2.dp, bottom = 2.dp, start = 5.dp, end = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (unreadCount >= 100) "99+" else unreadCount.toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

private fun formatTime(context: Context, lastActivityAt: Long?): String {
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

@Composable
@Preview(widthDp = 100, heightDp = 100, showBackground = true)
fun QuestionsMetaPreviewNull() {
    ErasMappTheme {
        QuestionsMeta(
            unreadCount = 10,
            lastActivityAt = null
        )
    }
}

@Composable
@Preview(widthDp = 100, heightDp = 100, showBackground = true)
fun QuestionsMetaPreviewToday() {
    ErasMappTheme {
        QuestionsMeta(
            unreadCount = 10,
            lastActivityAt = System.currentTimeMillis()
        )
    }
}

@Composable
@Preview(widthDp = 100, heightDp = 100, showBackground = true)
fun QuestionsMetaPreviewYesterday() {
    ErasMappTheme {
        QuestionsMeta(
            unreadCount = 10,
            lastActivityAt = System.currentTimeMillis() - DateUtils.DAY_IN_MILLIS
        )
    }
}

@Composable
@Preview(widthDp = 100, heightDp = 100, showBackground = true)
fun QuestionsMetaPreviewOlder() {
    ErasMappTheme {
        QuestionsMeta(
            unreadCount = 500,
            lastActivityAt = System.currentTimeMillis() - 3 * DateUtils.DAY_IN_MILLIS
        )
    }
}