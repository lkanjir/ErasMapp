package com.rampu.erasmapp.channels.ui.questions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.common.ui.components.ErrorMessage
import com.rampu.erasmapp.common.ui.components.LabeledInputField
import com.rampu.erasmapp.common.ui.components.LoadingIndicator
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun QuestionsScreen(
    channelId: String,
    channelTitle: String,
    onBack: () -> Unit,
    onOpenQuestion: (String) -> Unit,
    onEvent: (event: QuestionsEvent) -> Unit,
    state: QuestionsUiState
) {
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
            AlertDialog(
                onDismissRequest = {
                    onEvent(QuestionsEvent.ShowCreateDialog(false))
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onEvent(QuestionsEvent.CreateQuestion)
                        }
                    ) {
                        Text("Post")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            onEvent(QuestionsEvent.ShowCreateDialog(false))
                        }
                    ) {
                        Text("Cancel")
                    }
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        LabeledInputField(
                            value = state.newTitle,
                            onValueChange = {
                                onEvent(QuestionsEvent.TitleChanged(it))
                            },
                            label = "Title"
                        )
                        LabeledInputField(
                            value = state.newBody,
                            onValueChange = {
                                onEvent(QuestionsEvent.BodyChanged(it))
                            },
                            label = "Topic"
                        )
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
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = channelTitle,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.height(10.dp))
                    ErrorMessage(message = state.errorMsg)
                }
            }
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = channelTitle,
                        style = MaterialTheme.typography.headlineMedium
                    )
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
                FloatingActionButton(
                    onClick = { onEvent(QuestionsEvent.ShowCreateDialog(show = true)) },
                    modifier = Modifier.align(Alignment.BottomEnd),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add question")
                }
            }

        }
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
                        unreadCount = 10
                    )
                ),
                errorMsg = "Preview error"
            )
        )
    }
}