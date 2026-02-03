package com.rampu.erasmapp.news.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.common.ui.components.ErrorMessage
import com.rampu.erasmapp.common.ui.components.LoadingIndicator
import com.rampu.erasmapp.main.TopBarState
import com.rampu.erasmapp.news.domain.NewsItem
import com.rampu.erasmapp.news.ui.components.NewsCategoryFilter
import com.rampu.erasmapp.news.ui.components.NewsEditor
import com.rampu.erasmapp.news.ui.components.NewsListItem
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun NewsScreen(
    setTopBar: (String, TopBarState?) -> Unit,
    topBarOwnerId: String,
    onEvent: (event: NewsEvent) -> Unit,
    onOpenNews: (newsId: String) -> Unit,
    state: NewsUiState,
) {
    val context = LocalContext.current

    SideEffect {
        setTopBar(
            topBarOwnerId,
            TopBarState(
                title = "News"
            )
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            setTopBar(topBarOwnerId, null)
        }
    }

    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingIndicator()
            }
        }

        !state.errorMsg.isNullOrBlank() -> {
            ErrorMessage(message = state.errorMsg)
        }

        state.showEditor -> {
            NewsEditor(state = state, onEvent = onEvent)
        }

        else -> {
            val filteredNews = if (state.selectedTopic.isNullOrBlank()) state.news
            else state.news.filter { it.topic == state.selectedTopic }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    state.actionError?.let { ErrorMessage(message = it) }
                    NewsCategoryFilter(
                        selectedTopic = state.selectedTopic,
                        onSelected = { onEvent(NewsEvent.FilterChanged(it)) }
                    )
                    Spacer(Modifier.height(10.dp))

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (filteredNews.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No news yet",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Share an update to keep students informed.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (state.isAdmin) {
                                    Spacer(Modifier.height(16.dp))
                                    Button(
                                        onClick = { onEvent(NewsEvent.ShowEditor(null)) },
                                        enabled = !state.isSaving
                                    ) {
                                        Text("Create news")
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(filteredNews, key = { it.id }) { item ->
                                    NewsListItem(
                                        item = item,
                                        context = context,
                                        onClick = { onOpenNews(item.id) }
                                    )

                                }
                            }
                        }
                    }
                }

                if (state.isAdmin) {
                    ExtendedFloatingActionButton(
                        onClick = { onEvent(NewsEvent.ShowEditor(null)) },
                        text = { Text("Add news") },
                        icon = { Icon(Icons.Filled.Add, contentDescription = "Create news") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd).padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun NewsScreenPreview() {
    ErasMappTheme {
        NewsScreen(
            setTopBar = {_, _ -> },
            topBarOwnerId = "preview",
            onEvent = { },
            onOpenNews = { },
            state = NewsUiState(
                isLoading = false,
                isAdmin = true,
                news = listOf(
                    NewsItem(
                        id = "id",
                        title = "Title 1",
                        body = "Body 1",
                        topic = "topic 1",
                        isUrgent = false,
                        createdAt = System.currentTimeMillis(),
                        authorId = "author",
                        authorLabel = "lkanjir",
                        authorPhotoUrl = null,
                    )
                )
            )
        )
    }
}
