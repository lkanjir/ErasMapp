package com.rampu.erasmapp.channels.ui.channels

import android.graphics.Paint
import android.text.Layout
import android.text.format.DateUtils
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.textview.MaterialTextView
import com.rampu.erasmapp.R
import com.rampu.erasmapp.channels.domian.Channel
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun ChannelItem(channel: Channel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val icon = iconForKey(channel.iconKey)
    val letter = channel.title.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    Row(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = letter,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(
            Modifier.weight(1f)
        ) {
            Text(
                text = channel.title,
                style = MaterialTheme.typography.titleMedium,
            )
            if(!channel.description.isNullOrBlank()){
                Text(
                    text = channel.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            else{
                Text(
                    text = channel.topic,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(Modifier.width(6.dp))
        ChannelMeta(
            lastActivityAt = channel.lastActivityAt,
            unreadCount = channel.unreadCount,
        )
    }
}

@Composable
private fun iconForKey(iconKey: String?): ImageVector? {
    val resiId = when (iconKey) {
        "general" -> R.drawable.general
        "dorms" -> R.drawable.dorms
        "activities" -> R.drawable.activity
        "college" -> R.drawable.college
        "admin" -> R.drawable.admin
        "events" -> R.drawable.event
        "health" -> R.drawable.health
        "travel" -> R.drawable.travel
        else -> null
    }

    return resiId?.let { ImageVector.vectorResource(it) }
}

@Preview(heightDp = 100, showBackground = true)
@Composable
fun ChannelItemPreview() {
    ErasMappTheme() {
        ChannelItem(
            channel = Channel(
                id = "abcdefgh",
                title = "Erasmus Meetups",
                topic = "Events",
                description = "Meetups, hangouts, and social activities for Erasmus students.",
                createdBy = "lkanjir23",
                iconKey = null,
                lastActivityAt = System.currentTimeMillis() - 2* DateUtils.DAY_IN_MILLIS,
                unreadCount = 5,
            ),
            onClick = {}
        )
    }

}

@Preview(heightDp = 100, showBackground = true)
@Composable
fun ChannelItemPreview2() {
    ErasMappTheme() {
        ChannelItem(
            channel = Channel(
                id = "abcdefgh",
                title = "Erasmus Meetups",
                topic = "Events",
                description = "Meetups, hangouts, and social activities for Erasmus students.",
                createdBy = "lkanjir23",
                iconKey = "general",
                lastActivityAt = System.currentTimeMillis(),
                unreadCount = 100,
            ),
            onClick = {}
        )
    }

}
