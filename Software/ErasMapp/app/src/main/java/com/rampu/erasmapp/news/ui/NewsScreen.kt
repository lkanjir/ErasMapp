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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
                title = "News",
                actions = {
                    if (state.isAdmin && !state.showEditor) {
                        IconButton(
                            onClick = { onEvent(NewsEvent.ShowEditor(null)) },
                            enabled = !state.isSaving
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add news")
                        }
                    }
                }
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


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                state.actionError?.let { ErrorMessage(message = it) }
                NewsCategoryFilter(selectedTopic = state.selectedTopic, onSelected = {onEvent(
                    NewsEvent.FilterChanged(it))})
                Spacer(Modifier.height(10.dp))
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
