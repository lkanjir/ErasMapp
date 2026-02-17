package com.rampu.erasmapp.adminConsole

import android.app.DatePickerDialog
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rampu.erasmapp.eventCalendar.domain.CalendarEvent
import com.rampu.erasmapp.eventCalendar.ui.EventCalendarViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AdminEventsScreen(
    onBack: () -> Unit,
    viewModel: EventCalendarViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    var deleteEventDialog by remember { mutableStateOf<CalendarEvent?>(null) }

    val sortedEvents = remember(uiState.events) {
        uiState.events.sortedWith(compareBy<CalendarEvent> { it.date }.thenBy { it.title })
    }

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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Calendar events",
                    fontSize = 32.sp,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.W600
                )
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.setAddDialogVisible(true) },
            enabled = uiState.isAdmin && !uiState.isSaving
        ) {
            Text("Add new")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Text(uiState.errorMessage ?: "Unable to load events.")
            }

            sortedEvents.isEmpty() -> {
                Text("No events.")
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sortedEvents, key = { it.id }) { event ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = event.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Row() {
                                        TextButton(
                                            onClick = { viewModel.selectEvent(event) },
                                            enabled = uiState.isAdmin && !uiState.isSaving
                                        ) {
                                            Text("Edit")
                                        }
                                        TextButton(
                                            onClick = { deleteEventDialog = event },
                                            enabled = uiState.isAdmin && !uiState.isSaving
                                        ) {
                                            Text("Delete")
                                        }
                                    }

                                }
                                Text(
                                    text = "${event.date.format(dateFormatter)} at ${event.time}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (event.location.isNotBlank() && event.location != "-") {
                                    Text(
                                        text = event.location,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                if (event.description.isNotBlank() && event.description != "-") {
                                    Text(
                                        text = event.description,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.showAddCalendarEventDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setAddDialogVisible(false) },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.saveNewEvent() },
                    enabled = !uiState.isSaving
                ) {
                    Text(if (uiState.isSaving) "Saving..." else "Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.setAddDialogVisible(false) },
                    enabled = !uiState.isSaving
                ) { Text("Cancel") }
            },
            title = { Text("Add New Event") },
            text = {
                val addDialogScroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(addDialogScroll),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.newTitle,
                        onValueChange = viewModel::updateNewTitle,
                        label = { Text("Title") },
                        singleLine = true
                    )
                    DatePickerField(
                        value = uiState.newDateText,
                        label = "Date",
                        onDateSelected = viewModel::updateNewDateText
                    )
                    OutlinedTextField(
                        value = uiState.newTime,
                        onValueChange = viewModel::updateNewTime,
                        label = { Text("Time") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.newLocation,
                        onValueChange = viewModel::updateNewLocation,
                        label = { Text("Location") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.newDescription,
                        onValueChange = viewModel::updateNewDescription,
                        label = { Text("Description") },
                        singleLine = true
                    )
                }
            }
        )
    }

    if (uiState.showEditCalendarEventDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissEditDialog() },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.saveEditedEvent() },
                    enabled = !uiState.isSaving
                ) {
                    Text(if (uiState.isSaving) "Saving..." else "Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissEditDialog() },
                    enabled = !uiState.isSaving
                ) { Text("Cancel") }
            },
            title = { Text("Update Event") },
            text = {
                val addDialogScroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(addDialogScroll),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.editTitle,
                        onValueChange = viewModel::updateEditTitle,
                        label = { Text("Title") },
                        singleLine = true
                    )
                    DatePickerField(
                        value = uiState.editDateText,
                        label = "Date",
                        onDateSelected = viewModel::updateEditDateText
                    )
                    OutlinedTextField(
                        value = uiState.editTime,
                        onValueChange = viewModel::updateEditTime,
                        label = { Text("Time") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.editLocation,
                        onValueChange = viewModel::updateEditLocation,
                        label = { Text("Location") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.editDescription,
                        onValueChange = viewModel::updateEditDescription,
                        label = { Text("Description") },
                        singleLine = true
                    )
                }
            }
        )
    }

    deleteEventDialog?.let { event ->
        AlertDialog(
            onDismissRequest = { deleteEventDialog = null },
            title = { Text("Delete event?") },
            text = { Text("Are you sure you want to delete: ${event.title}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteEventDialog = null
                        viewModel.deleteSelectedEvent(event.id)
                    },
                    enabled = !uiState.isSaving
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { deleteEventDialog = null },
                    enabled = !uiState.isSaving
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DatePickerField(
    value: String,
    label: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val initialDate = remember(value) {
        runCatching { LocalDate.parse(value) }.getOrElse { LocalDate.now() }
    }
    val openPicker = {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val date = LocalDate.of(year, month + 1, day)
                onDateSelected(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
            },
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        ).show()
    }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            singleLine = true,
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = openPicker) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Pick date")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { openPicker() }
        )
    }
}