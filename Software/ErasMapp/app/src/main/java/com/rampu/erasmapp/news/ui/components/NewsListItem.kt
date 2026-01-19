package com.rampu.erasmapp.news.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.common.ui.components.UserAvatar
import com.rampu.erasmapp.common.util.formatTime
import com.rampu.erasmapp.news.domain.NewsItem
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun NewsListItem(item: NewsItem, onClick: () -> Unit, context: Context) {
    val markerColor =
        if (item.isUrgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surface.copy(
            alpha = 0f
        )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .height(intrinsicSize = IntrinsicSize.Min)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(markerColor)
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val authorLabel = item.authorLabel?.ifBlank { "Staff" } ?: "Staff"
                UserAvatar(label = authorLabel, photoUrl = item.authorPhotoUrl, size = 36.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            NewsBadge(text = getNewsTopicLabel(item.topic))
                            NewsBadge(text = formatTime(context, item.createdAt))
                        }
                    }
                    Text(
                        text = item.body,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun NewsListItemPreview() {
    ErasMappTheme {
        NewsListItem(
            item = NewsItem(
                id = "id",
                title = "Test title",
                body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed quis lectus justo. Donec vitae felis pharetra ex vehicula aliquet vel ut est. Nunc rhoncus urna hendrerit nunc fermentum, consectetur sollicitudin nunc convallis. Quisque in vulputate dui, a bibendum urna. Aliquam sagittis iaculis arcu vel posuere. Quisque congue sem at ex tempus bibendum. Suspendisse porta ac ante ultrices feugiat. Etiam imperdiet nunc eget luctus porttitor. Suspendisse ullamcorper quis neque eu consectetur. Aenean malesuada risus in convallis vestibulum. In hac habitasse platea dictumst. Curabitur tincidunt, nisi in dapibus accumsan, ante lorem scelerisque turpis, vel pretium lacus ante ut erat. Aliquam viverra est nec nulla finibus sodales. Curabitur ipsum nunc, varius ut elit eu, finibus vulputate tellus. Vestibulum eget odio suscipit, pretium sem nec, laoreet magna. Nam et lectus sit amet lectus ultrices suscipit. Nulla quis massa ac lectus dictum vehicula eu a orci. Nullam at imperdiet ligula. Pellentesque pharetra varius lectus, nec maximus nibh efficitur eget. ",
                topic = "test topic",
                isUrgent = true,
                createdAt = System.currentTimeMillis(),
                authorId = "authorId",
                authorLabel = "lkanjir",
                authorPhotoUrl = null,
            ), onClick = {}, context = LocalContext.current
        )
    }
}