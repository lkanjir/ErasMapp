package com.rampu.erasmapp.channels.ui.channels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.channels.domian.Channel
import com.rampu.erasmapp.common.ui.components.UserAvatar
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun ChannelItem(channel: Channel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val icon = channelIconForKey(channel.iconKey)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                RoundedCornerShape(10.dp)
            )
            .padding(12.dp)
    ) {
        UserAvatar(
            label = channel.title,
            icon = icon,
            size = 44.dp,
        )
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = channel.title,
                style = MaterialTheme.typography.titleMedium
            )
            if (!channel.description.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = channel.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(heightDp = 200, showBackground = true)
@Composable
fun ChannelItemPreview() {
    ErasMappTheme {
        ChannelItem(
            channel = Channel(
                id = "abcdefgh",
                title = "Erasmus Meetups",
                topic = "Events",
                description = "Meetups, hangouts, and social activities for Erasmus students.",
                createdBy = "lkanjir23",
                iconKey = null,
            ),
            onClick = {}
        )
    }

}

@Preview(heightDp = 200, showBackground = true)
@Composable
fun ChannelItemPreview2() {
    ErasMappTheme {
        ChannelItem(
            channel = Channel(
                id = "abcdefgh",
                title = "Erasmus Meetups",
                topic = "Events",
                description = "Meetups, hangouts, and social activities for Erasmus students.",
                createdBy = "lkanjir23",
                iconKey = "general",
            ),
            onClick = {}
        )
    }

}
