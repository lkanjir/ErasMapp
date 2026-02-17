package com.rampu.erasmapp.news.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.common.ui.components.ErrorMessage
import com.rampu.erasmapp.common.ui.components.LoadingIndicator
import com.rampu.erasmapp.common.ui.components.UserAvatar
import com.rampu.erasmapp.common.util.formatTime
import com.rampu.erasmapp.news.domain.NewsItem
import com.rampu.erasmapp.news.ui.components.NewsBadge
import com.rampu.erasmapp.news.ui.components.NewsEditor
import com.rampu.erasmapp.news.ui.components.getNewsTopicLabel
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    onBack: () -> Unit, onEvent: (event: NewsEvent) -> Unit, newsId: String, state: NewsUiState
) {
    val item = state.news.firstOrNull { it.id == newsId }
    val context = LocalContext.current
    var deleteTarget by remember { mutableStateOf<NewsItem?>(null) }
    val shouldReturn = !state.isLoading && state.errorMsg.isNullOrBlank() && item == null
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(shouldReturn) {
        if (shouldReturn) onBack()
    }

    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        }

        !state.errorMsg.isNullOrBlank() || item == null -> {
            ErrorMessage(message = state.errorMsg ?: "News item not found")
        }

        state.showEditor -> {
            NewsEditor(state = state, onEvent = onEvent)
        }

        else -> {
            val markerColor =
                if (item.isUrgent) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.surface.copy(alpha = 0f)
            val authorLabel = item.authorLabel?.ifBlank { "Staff" } ?: "Staff"
            val topicLabel = getNewsTopicLabel(item.topic)
            val headerModifier = if (state.isAdmin) Modifier.combinedClickable(
                onClick = {},
                onLongClick = { showBottomSheet = true })
            else Modifier

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "News",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    if (state.isAdmin) {
                        IconButton(onClick = { showBottomSheet = true }) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                contentDescription = "More actions"
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(headerModifier)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .fillMaxHeight()
                                .background(markerColor)
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                UserAvatar(
                                    label = authorLabel,
                                    photoUrl = item.authorPhotoUrl,
                                    size = 36.dp
                                )
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = authorLabel,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        NewsBadge(text = topicLabel)
                                        NewsBadge(text = formatTime(context, item.createdAt))
                                    }
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = item.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                }

                Spacer(Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Details",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = item.body,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet && state.isAdmin && item != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "More actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                ListItem(
                    headlineContent = { Text("Edit") },
                    leadingContent = { Icon(Icons.Outlined.Edit, contentDescription = "Edit") },
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = {
                            showBottomSheet = false
                            onEvent(NewsEvent.ShowEditor(item))
                        })
                )
                ListItem(
                    headlineContent = { Text("Delete") },
                    leadingContent = { Icon(Icons.Outlined.Delete, contentDescription = "Delete") },
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = {
                            showBottomSheet = false
                            deleteTarget = item
                        })
                )
            }
        }
    }

    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete news?") },
            text = { Text("Are you sure you want to delete: ${target.title}?") },
            confirmButton = {
                Button(
                    onClick = {
                        deleteTarget = null
                        onEvent(NewsEvent.DeleteNews(target.id))
                    }, enabled = !state.isSaving
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.onError)
                }
            }, dismissButton = {
                TextButton(
                    onClick = { deleteTarget = null },
                    enabled = !state.isSaving
                ) { Text("Cancel") }
            })
    }
}

@Composable
@Preview(showBackground = true)
fun NewsDetailPreview() {
    ErasMappTheme {
        NewsDetailScreen(
            onBack = {},
            onEvent = { },
            newsId = "id",
            state = NewsUiState(
                news = listOf(
                    NewsItem(
                        id = "id",
                        title = "Title",
                        body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin mollis leo a velit malesuada, pulvinar dapibus neque molestie. Sed ultricies dui mi, a mattis risus vestibulum quis. Nullam egestas tortor massa, ut interdum massa facilisis sed. Etiam sed eros a sem tempus consectetur. Sed maximus ante rutrum magna malesuada porta. Duis nec nunc risus. Nullam ornare quam sit amet ipsum bibendum, at pulvinar tortor eleifend. Ut laoreet nisi libero, et tempus felis maximus vitae. Morbi et enim aliquet, convallis sem at, lacinia ipsum. Suspendisse eget ipsum vitae augue scelerisque commodo. Quisque at justo blandit, fermentum turpis ultricies, ullamcorper ante. Donec. ",
                        topic = "Topic",
                        isUrgent = true,
                        createdAt = System.currentTimeMillis(),
                        authorId = "authorId",
                        authorLabel = "lkanjir",
                        authorPhotoUrl = null,
                    )
                ),
                isLoading = false,
                errorMsg = null,
                isSignedOut = false,
                isSaving = false,
                isAdmin = true,
                actionError = null,
                editId = "id",
                editTitle = "",
            ),
        )
    }
}
