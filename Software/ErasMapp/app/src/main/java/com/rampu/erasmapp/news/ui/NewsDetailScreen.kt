package com.rampu.erasmapp.news.ui

import androidx.compose.foundation.background
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.common.ui.components.ErrorMessage
import com.rampu.erasmapp.common.ui.components.LoadingIndicator
import com.rampu.erasmapp.common.util.formatTime
import com.rampu.erasmapp.news.domain.NewsItem
import com.rampu.erasmapp.news.ui.components.NewsEditor
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    onBack: () -> Unit, onEvent: (event: NewsEvent) -> Unit, newsId: String, state: NewsUiState
) {
    val item = state.news.firstOrNull { it.id == newsId }
    val context = LocalContext.current;
    var deleteTarget by remember { mutableStateOf<NewsItem?>(null) }

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
                if (item.isUrgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surface.copy(
                    alpha = 0f
                )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "News",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium)
                )
                Spacer(Modifier.height(10.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
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
                            Text(text = item.title, style = MaterialTheme.typography.titleLarge)
                            Text(
                                text = item.topic,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = formatTime(context, item.createdAt),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(6.dp))
                            Text(text = item.body, style = MaterialTheme.typography.bodyLarge)

                            if (state.isAdmin) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                ) {
                                    TextButton(
                                        onClick = { onEvent(NewsEvent.ShowEditor(item)) },
                                        enabled = !state.isSaving
                                    ) {
                                        Text("Edit")
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Button(
                                        onClick = { deleteTarget = item },
                                        enabled = !state.isSaving,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Text("Delete", color = MaterialTheme.colorScheme.onError)
                                    }
                                }
                            }
                        }
                    }
                }
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
                        body = "body",
                        topic = "Topic",
                        isUrgent = false,
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
