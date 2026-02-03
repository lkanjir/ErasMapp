package com.rampu.erasmapp.channels.ui.questions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import com.rampu.erasmapp.channels.domian.QuestionStatus
import com.rampu.erasmapp.common.ui.components.DialogConfirmButton
import com.rampu.erasmapp.common.ui.components.DialogDismissButton
import com.rampu.erasmapp.common.ui.components.ErrorMessage
import com.rampu.erasmapp.common.ui.components.LabeledInputField
import com.rampu.erasmapp.common.ui.components.LoadingIndicator
import com.rampu.erasmapp.main.TopBarState
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun QuestionsScreen(
    channelId: String,
    channelTitle: String,
    onBack: () -> Unit,
    setTopBar: (String, TopBarState?) -> Unit,
    topBarOwnerId: String,
    onOpenQuestion: (String) -> Unit,
    onEvent: (event: QuestionsEvent) -> Unit,
    state: QuestionsUiState
) {
    val showTitleError = state.newTitle.isBlank()
    val showBodyError = state.newBody.isBlank()

    SideEffect {
        setTopBar(
            topBarOwnerId,
            TopBarState(
                title = channelTitle,
                onNavigateUp = onBack
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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        }

        state.showCreateDialog -> {
            val canSubmit = !showTitleError && !showBodyError && !state.isSaving
            AlertDialog(
                onDismissRequest = {
                    onEvent(QuestionsEvent.ShowCreateDialog(false))
                },
                title = {
                    Text(text = "New question", style = MaterialTheme.typography.titleLarge)
                },
                confirmButton = {
                    DialogConfirmButton(
                        text = "Post",
                        onClick = { onEvent(QuestionsEvent.CreateQuestion) },
                        enabled = canSubmit
                    )
                },
                dismissButton = {
                    DialogDismissButton(
                        text = "Cancel",
                        onClick = { onEvent(QuestionsEvent.ShowCreateDialog(false)) }
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        Text(
                            text = "Be clear and specific so others can answer quickly.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LabeledInputField(
                            value = state.newTitle,
                            onValueChange = {
                                onEvent(QuestionsEvent.TitleChanged(it))
                            },
                            label = "Title",
                            placeholder = "e.g., How do I ...",
                            supportingText = {
                                Text(if (showTitleError) "Title is required." else "Keep it short and specific.")
                            },
                            isError = showTitleError,
                            enabled = !state.isSaving
                        )
                        Column {
                            Text(
                                text = "Body",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(Modifier.height(6.dp))
                            TextField(
                                value = state.newBody,
                                onValueChange = { onEvent(QuestionsEvent.BodyChanged(it)) },
                                placeholder = { Text("Ask a clear, specific question") },
                                shape = RoundedCornerShape(5.dp),
                                isError = showBodyError,
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                maxLines = 8,
                                enabled = !state.isSaving
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = if (showBodyError) "Body is required." else "Add context and what you already tried.",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (showBodyError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        }

        !state.errorMsg.isNullOrBlank() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                ErrorMessage(message = state.errorMsg)
            }
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    QuestionFilterTabs(
                        selected = state.filter,
                        onSelected = { onEvent(QuestionsEvent.FilterChanged(it)) })
                    Spacer(Modifier.height(10.dp))
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 72.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.questions, key = { it.id }) { item ->
                            QuestionItem(item = item, onClick = { onOpenQuestion(item.id) })
                        }
                    }
                }
                ExtendedFloatingActionButton(
                    onClick = { onEvent(QuestionsEvent.ShowCreateDialog(show = true)) },
                    text = { Text("Ask question") },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Add question") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                )
            }

        }
    }
}

@Composable
fun QuestionFilterTabs(
    selected: QuestionFilter,
    onSelected: (QuestionFilter) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(QuestionFilter.entries, key = { it.name }) { filter ->
            FilterChip(
                selected = filter == selected,
                onClick = { onSelected(filter) },
                label = { Text(filter.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun QuestionFilterTabsPreview() {
    ErasMappTheme {
        QuestionFilterTabs(
            selected = QuestionFilter.OPEN,
            onSelected = {}
        )
    }
}


@Preview(showBackground = true)
@Composable
fun QuestionScreenPreview() {
    ErasMappTheme {
        QuestionsScreen(
            channelId = "asdf",
            channelTitle = "Activites",
            onBack = {},
            setTopBar = { _, _ -> },
            topBarOwnerId = "preview",
            onOpenQuestion = {},
            onEvent = {},
            state = QuestionsUiState(
                isLoading = false,
                channelId = "asdf",
                channelTitle = "Activities",
                questions = listOf(
                    QuestionListItem(
                        id = "id",
                        title = "Preview Title",
                        bodyPreview = "Body preview",
                        authorLabel = "Author name",
                        authorPhotoUrl = null,
                        lastActivityAt = System.currentTimeMillis(),
                        unreadCount = 10,
                        status = QuestionStatus.OPEN
                    )
                ),
                errorMsg = null
            )
        )
    }
}
