package com.rampu.erasmapp.news.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.channels.ui.channels.channelIconLabelForKey
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun NewsBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Box(
        modifier = modifier
            .background(containerColor, CircleShape)
            .padding(top = 2.dp, bottom = 2.dp, start = 5.dp, end = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

fun getNewsTopicLabel(topic: String?) = channelIconLabelForKey(topic) ?: "General"

@Preview(showBackground = true)
@Composable
fun NewsBadgePreview() {
    ErasMappTheme {
        NewsBadge(
            text = "Test",
        )
    }
}
