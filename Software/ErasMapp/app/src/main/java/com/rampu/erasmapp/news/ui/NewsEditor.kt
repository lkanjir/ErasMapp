package com.rampu.erasmapp.news.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.common.ui.components.ErrorMessage
import com.rampu.erasmapp.common.ui.components.LabeledInputField
import com.rampu.erasmapp.common.ui.components.LoadingIndicator
import com.rampu.erasmapp.news.domain.NewsItem
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsEditor(state: NewsUiState, onEvent: (NewsEvent) -> Unit) {
    val isEdit = !state.editId.isNullOrBlank()

    AlertDialog(
        onDismissRequest = { onEvent(NewsEvent.DismissEditor) },
        confirmButton = {
            Button(onClick = { onEvent(NewsEvent.SaveNews) }, enabled = !state.isSaving) {
                if (state.isSaving) {
                    LoadingIndicator()
                } else Text("Save")
            }
        }, dismissButton = {
            Button(onClick = { onEvent(NewsEvent.DismissEditor) }, enabled = !state.isSaving) {
                Text("Cancel")
            }
        },
        title = { Text(if (isEdit) "Edit news" else "Add news") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LabeledInputField(
                    value = state.editTitle,
                    onValueChange = { onEvent(NewsEvent.TitleChanged(it)) },
                    label = "Title",
                    enabled = !state.isSaving
                )
                LabeledInputField(
                    value = state.editTopic,
                    onValueChange = { onEvent(NewsEvent.TopicChanged(it)) },
                    label = "Topic",
                    enabled = !state.isSaving
                )
                OutlinedTextField(
                    value = state.editBody,
                    onValueChange = { onEvent(NewsEvent.BodyChanged(it)) },
                    label = { Text("Body") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 8,
                    enabled = !state.isSaving,
                    placeholder = { Text("Body") }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Urgent")
                    Switch(
                        checked = state.editUrgent,
                        onCheckedChange = { onEvent(NewsEvent.UrgentChanged(it)) },
                        enabled = !state.isSaving
                    )
                }
                state.editorError?.let{ ErrorMessage(message = it) }
            }
        }
    )


}

@Composable
@Preview(showBackground = true)
fun NewsEditorPreview() {
    ErasMappTheme {
        NewsEditor(
            state = NewsUiState(
                news = listOf(
                    NewsItem(
                        id = "id",
                        title = "title",
                        body = "Body",
                        topic = "topic",
                        isUrgent = false,
                        createdAt = System.currentTimeMillis(),
                        authorId = "authorId",
                    )
                ),
                isLoading = false,
                errorMsg = null,
                isSignedOut = false,
                isSaving = false,
                isAdmin = true,
                actionError = null,
                editId = "id"
            ), onEvent = {}
        )
    }
}