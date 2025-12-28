package com.rampu.erasmapp.channels.ui.questions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(label = item.authorLabel, photoUrl = item.authorPhotoUrl)
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.bodyPreview,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.width(12.dp))
        QuestionsMeta(
            lastActivityAt = item.lastActivityAt,
            unreadCount = item.unreadCount,
            status = item.status
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