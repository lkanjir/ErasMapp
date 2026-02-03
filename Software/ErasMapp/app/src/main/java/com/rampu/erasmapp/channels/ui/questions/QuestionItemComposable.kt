package com.rampu.erasmapp.channels.ui.questions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.channels.domian.QuestionStatus
import com.rampu.erasmapp.common.ui.components.UserAvatar
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun QuestionItem(item: QuestionListItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                UserAvatar(label = item.authorLabel, photoUrl = item.authorPhotoUrl, size = 40.dp)
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.bodyPreview,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.authorLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        StatusPill(status = item.status)
                    }
                }
                Spacer(Modifier.width(12.dp))
                QuestionsMeta(
                    lastActivityAt = item.lastActivityAt,
                    unreadCount = item.unreadCount,
                    status = item.status
                )
            }
        }
    }
}

@Composable
private fun StatusPill(status: QuestionStatus) {
    val (label, bg, fg) = when (status) {
        QuestionStatus.OPEN -> Triple(
            "Open",
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.primary
        )
        QuestionStatus.ANSWERED -> Triple(
            "Answered",
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f),
            MaterialTheme.colorScheme.secondary
        )
        QuestionStatus.LOCKED -> Triple(
            "Locked",
            MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.error
        )
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuestionItemPreview() {
    ErasMappTheme {
        QuestionItem(
            onClick = {},
            item = QuestionListItem(
                id = "id",
                title = "Preview question title",
                bodyPreview = "Body preview text",
                authorLabel = "Author name",
                authorPhotoUrl = null,
                lastActivityAt = System.currentTimeMillis(),
                unreadCount = 10,
                status = QuestionStatus.OPEN
            ),
        )
    }
}
