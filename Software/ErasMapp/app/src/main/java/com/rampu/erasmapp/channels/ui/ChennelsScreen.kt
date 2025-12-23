@file:OptIn(ExperimentalMaterial3Api::class)

package com.rampu.erasmapp.channels.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.channels.domian.Channel
import com.rampu.erasmapp.common.ui.StandardPreview
import com.rampu.erasmapp.common.ui.components.LabeledInputField
import com.rampu.erasmapp.common.ui.components.LoadingIndicator

@Composable
fun ChannelsScreen(
    onBack: () -> Unit,
    onEvent: (event: ChannelEvent) -> Unit,
    state: ChannelUiState
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
                    onEvent(ChannelEvent.ShowCreateDialog(false))
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onEvent(ChannelEvent.CreateChannel)
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            onEvent(ChannelEvent.ShowCreateDialog(false))
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
                                onEvent(ChannelEvent.TitleChanged(it))
                            },
                            label = "Title"
                        )
                        LabeledInputField(
                            value = state.newTopic,
                            onValueChange = {
                                onEvent(ChannelEvent.TopicChanged(it))
                            },
                            label = "Topic"
                        )
                        LabeledInputField(
                            value = state.newDescription,
                            onValueChange = {
                                onEvent(ChannelEvent.DescriptionChanged(it))
                            },
                            label = "Description"
                        )
                    }
                }
            )
        }

        else -> {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    items(state.channels, key = { it.id }) { channel ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = channel.title)
                        }
                    }

                }
                Button(
                    onClick = {
                        onEvent(ChannelEvent.ShowCreateDialog(true))
                    }
                ) {
                    Text("Add channel")
                }
            }
        }
    }
}

@StandardPreview
@Composable
fun ChannelScreenPreview(){
    ChannelsScreen(
        onBack = {},
        onEvent = {},
        state = ChannelUiState(
            isLoading = false,
            showCreateDialog = false,
            channels = listOf(
                Channel(
                    id = "asdf",
                    title = "Preview title",
                    topic = "Preview topic",
                    description = "Preview Description",
                    createdBy = "Created by HERE"
                )
            )
        )
    )

}