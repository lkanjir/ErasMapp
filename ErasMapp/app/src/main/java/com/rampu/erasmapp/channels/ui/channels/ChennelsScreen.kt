@file:OptIn(ExperimentalMaterial3Api::class)

package com.rampu.erasmapp.channels.ui.channels

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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.channels.domian.Channel
import com.rampu.erasmapp.common.ui.components.DialogConfirmButton
import com.rampu.erasmapp.common.ui.components.DialogDismissButton
import com.rampu.erasmapp.common.ui.components.ErrorMessage
import com.rampu.erasmapp.common.ui.components.LabeledInputField
import com.rampu.erasmapp.common.ui.components.LoadingIndicator
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun ChannelsScreen(
    onChannelSelected: (String, String) -> Unit,
    onEvent: (event: ChannelEvent) -> Unit,
    state: ChannelUiState
) {
    val showTitleError = state.newTitle.isBlank()
    val showDescriptionError = state.newDescription.isBlank()
    val canSubmit = !showTitleError && !showDescriptionError

    if (state.showCreateDialog && state.isAdmin) {
        AlertDialog(
            onDismissRequest = {
                onEvent(ChannelEvent.ShowCreateDialog(false))
            },
            confirmButton = {
                DialogConfirmButton(
                    text = "Save",
                    onClick = {
                        onEvent(ChannelEvent.CreateChannel)
                    },
                    enabled = canSubmit
                )
            },
            dismissButton = {
                DialogDismissButton(
                    text = "Cancel",
                    onClick = {
                        onEvent(ChannelEvent.ShowCreateDialog(false))
                    }
                )
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
                        label = "Title",
                        placeholder = "e.g., Housing tips",
                        isError = showTitleError,
                        supportingText = if (showTitleError) {
                            { Text("Title is required.") }
                        } else null
                    )
                    LabeledInputField(
                        value = state.newDescription,
                        onValueChange = {
                            onEvent(ChannelEvent.DescriptionChanged(it))
                        },
                        label = "Description",
                        placeholder = "eg. Dorms, ...",
                        isError = showDescriptionError,
                        supportingText = if (showDescriptionError) {
                            { Text("Description is required.") }
                        } else null
                    )
                    Column {
                        Text(
                            text = "Icon",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.height(6.dp))
                        ChannelIconPicker(
                            selectedKey = state.newIconKey,
                            onSelected = { onEvent(ChannelEvent.IconChanged(it)) }
                        )
                    }
                }
            }
        )
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

        !state.errorMsg.isNullOrBlank() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorMessage(message = state.errorMsg)
            }
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                if (state.channels.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No channels yet",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Start a new channel by tapping on the button in the lower right corner.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (state.isAdmin) {
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { onEvent(ChannelEvent.ShowCreateDialog(true)) }
                            ) {
                                Text("Create channel")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(state.channels, key = { it.id }) { channel ->
                            ChannelItem(
                                channel,
                                onClick = { onChannelSelected(channel.id, channel.title) })
                        }
                    }
                }

                if (state.isAdmin) {
                    ExtendedFloatingActionButton(
                        onClick = { onEvent(ChannelEvent.ShowCreateDialog(true)) },
                        text = { Text("New channel") },
                        icon = { Icon(Icons.Filled.Add, contentDescription = "Create channel") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}


@Preview(showSystemUi = false, showBackground = true)
@Composable
fun ChannelScreenPreview() {
    ErasMappTheme {
        ChannelsScreen(
            onEvent = {},
            onChannelSelected = { _, _ -> },
            state = ChannelUiState(
                isLoading = false,
                showCreateDialog = false,
                channels = listOf(
                    Channel(
                        id = "asdf",
                        title = "Preview title",
                        topic = "Preview topic",
                        description = "Preview Description Preview Description Preview Description Preview Description Preview DescriptionPreview DescriptionPreview DescriptionPreview DescriptionPreview DescriptionPreview DescriptionPreview DescriptionPreview DescriptionPreview DescriptionPreview DescriptionPreview DescriptionPreview DescriptionPreview DescriptionPreview Description",
                        createdBy = "Created by HERE",
                        iconKey = null,
                    ),
                    Channel(
                        id = "asdfdsafadsf",
                        title = "Preview title",
                        topic = "Preview topic",
                        description = "Preview Description",
                        createdBy = "Created by HERE",
                        iconKey = null,
                    ),
                    Channel(
                        id = "afdsafsa",
                        title = "Preview title",
                        topic = "Preview topic",
                        description = "Preview Description",
                        createdBy = "Created by HERE",
                        iconKey = null,
                    ),
                    Channel(
                        id = "asdfe",
                        title = "Preview title",
                        topic = "Preview topic",
                        description = "Preview Description",
                        createdBy = "Created by HERE",
                        iconKey = null,
                    )
                )
            )
        )
    }
}
