package com.rampu.erasmapp.channels.ui.questions

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.R
import com.rampu.erasmapp.channels.domian.QuestionStatus
import com.rampu.erasmapp.common.util.formatTime
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun QuestionsMeta(
    lastActivityAt: Long?,
    unreadCount: Long,
    status: QuestionStatus,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(verticalAlignment = Alignment.CenterVertically) {
        if (status == QuestionStatus.LOCKED) {
            Icon(
                painterResource(R.drawable.lock),
                contentDescription = "Lock",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(4.dp))
        }

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

}

@Composable
@Preview(widthDp = 100, heightDp = 100, showBackground = true)
fun QuestionsMetaPreviewNull() {
    ErasMappTheme {
        QuestionsMeta(
            unreadCount = 10,
            lastActivityAt = null,
            status = QuestionStatus.OPEN
        )
    }
}

@Composable
@Preview(widthDp = 100, heightDp = 100, showBackground = true)
fun QuestionsMetaPreviewToday() {
    ErasMappTheme {
        QuestionsMeta(
            unreadCount = 10,
            lastActivityAt = System.currentTimeMillis(),
            status = QuestionStatus.OPEN
        )
    }
}

@Composable
@Preview(widthDp = 100, heightDp = 100, showBackground = true)
fun QuestionsMetaPreviewYesterday() {
    ErasMappTheme {
        QuestionsMeta(
            unreadCount = 10,
            lastActivityAt = System.currentTimeMillis() - DateUtils.DAY_IN_MILLIS,
            status = QuestionStatus.LOCKED
        )
    }
}

@Composable
@Preview(widthDp = 100, heightDp = 100, showBackground = true)
fun QuestionsMetaPreviewOlder() {
    ErasMappTheme {
        QuestionsMeta(
            unreadCount = 500,
            lastActivityAt = System.currentTimeMillis() - 3 * DateUtils.DAY_IN_MILLIS,
            status = QuestionStatus.ANSWERED
        )
    }
}