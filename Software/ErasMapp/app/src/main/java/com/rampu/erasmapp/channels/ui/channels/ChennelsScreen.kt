@file:OptIn(ExperimentalMaterial3Api::class)

package com.rampu.erasmapp.channels.ui.channels

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.channels.domian.Channel
import com.rampu.erasmapp.common.ui.StandardPreview
import com.rampu.erasmapp.common.ui.components.LabeledInputField
import com.rampu.erasmapp.common.ui.components.LoadingIndicator
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun ChannelsScreen(
    onBack: () -> Unit,
    onChannelSelected: (String, String) -> Unit,
    onEvent: (event: ChannelEvent) -> Unit,
    state: ChannelUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),

        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Channels",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium)
            )
            IconButton(
                onClick = {}
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add channel"
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(state.channels, key = { it.id }) { channel ->
                ChannelItem(
                    channel,
                    onClick = {},
                    Modifier.clickable { onChannelSelected(channel.id, channel.title) })
            }
        }
    }


//    when {
//        state.isLoading -> {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                LoadingIndicator()
//            }
//        }
//
//        state.showCreateDialog -> {
//            AlertDialog(
//                onDismissRequest = {
//                    onEvent(ChannelEvent.ShowCreateDialog(false))
//                },
//                confirmButton = {
//                    Button(
//                        onClick = {
//                            onEvent(ChannelEvent.CreateChannel)
//                        }
//                    ) {
//                        Text("Save")
//                    }
//                },
//                dismissButton = {
//                    Button(
//                        onClick = {
//                            onEvent(ChannelEvent.ShowCreateDialog(false))
//                        }
//                    ) {
//                        Text("Cancel")
//                    }
//                },
//                text = {
//                    Column(
//                        verticalArrangement = Arrangement.spacedBy(15.dp)
//                    ) {
//                        LabeledInputField(
//                            value = state.newTitle,
//                            onValueChange = {
//                                onEvent(ChannelEvent.TitleChanged(it))
//                            },
//                            label = "Title"
//                        )
//                        LabeledInputField(
//                            value = state.newTopic,
//                            onValueChange = {
//                                onEvent(ChannelEvent.TopicChanged(it))
//                            },
//                            label = "Topic"
//                        )
//                        LabeledInputField(
//                            value = state.newDescription,
//                            onValueChange = {
//                                onEvent(ChannelEvent.DescriptionChanged(it))
//                            },
//                            label = "Description"
//                        )
//                    }
//                }
//            )
//        }
//
//        else -> {
//            Column(modifier = Modifier.fillMaxSize()) {
//                LazyColumn(
//                    modifier = Modifier.weight(1f),
//                    verticalArrangement = Arrangement.spacedBy(15.dp)
//                ) {
//                    items(state.channels, key = { it.id }) { channel ->
//                        Card(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable{
//                                    onChannelSelected(channel.id, channel.title)
//                                }
//                        ) {
//                            Text(text = channel.title)
//                        }
//                    }
//
//                }
//                Button(
//                    onClick = {
//                        onEvent(ChannelEvent.ShowCreateDialog(true))
//                    }
//                ) {
//                    Text("Add channel")
//                }
//            }
//        }
//    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun ChannelScreenPreview() {
    ErasMappTheme {
        ChannelsScreen(
            onBack = {},
            onEvent = {},
            onChannelSelected = { channelId, channelTitle -> },
            state = ChannelUiState(
                isLoading = false,
                showCreateDialog = false,
                channels = listOf(
                    Channel(
                        id = "asdf",
                        title = "Preview title",
                        topic = "Preview topic",
                        description = "Preview Description",
                        createdBy = "Created by HERE",
                        iconKey = null,
                        lastActivityAt = System.currentTimeMillis(),
                        unreadCount = 5,
                    ),
                    Channel(
                        id = "asdfdsafadsf",
                        title = "Preview title",
                        topic = "Preview topic",
                        description = "Preview Description",
                        createdBy = "Created by HERE",
                        iconKey = null,
                        lastActivityAt = System.currentTimeMillis(),
                        unreadCount = 5,
                    ),
                    Channel(
                        id = "afdsafsa",
                        title = "Preview title",
                        topic = "Preview topic",
                        description = "Preview Description",
                        createdBy = "Created by HERE",
                        iconKey = null,
                        lastActivityAt = System.currentTimeMillis(),
                        unreadCount = 5,
                    ),
                    Channel(
                        id = "asdfe",
                        title = "Preview title",
                        topic = "Preview topic",
                        description = "Preview Description",
                        createdBy = "Created by HERE",
                        iconKey = null,
                        lastActivityAt = System.currentTimeMillis(),
                        unreadCount = 100,
                    )
                )
            )
        )
    }
}