package com.rampu.erasmapp.schedule.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.main.TopBarState
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    setTopBar: (String, TopBarState?) -> Unit,
    topBarOwnerId: String,
    onBack: (() -> Unit)? = null
) {
    val viewModel: ScheduleViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val events = uiState.events
    val showDialog = uiState.showAddEventDialog
    val selectedEvent = uiState.selectedEvent
    val categories = listOf("Lecture", "Exercises", "Mid-Term", "Final-Exam", "Seminary", "Other")

    val dayFormatter = remember { DateTimeFormatter.ofPattern("EEEE, d.MM.yyyy", Locale.getDefault()) }
    val weekFormatterNoYear = remember { DateTimeFormatter.ofPattern("d.MM", Locale.getDefault()) }
    val weekFormatterWithYear = remember { DateTimeFormatter.ofPattern("d.MM.yyyy", Locale.getDefault()) }

    val currentWeekStart = uiState.currentWeekStart
    val currentDay = uiState.currentDay
    val isWeeklyView = uiState.isWeeklyView
    val weekEnd = currentWeekStart.plusDays(6)
    val weekLabel = if (currentWeekStart.year == weekEnd.year) {
        "${currentWeekStart.format(weekFormatterNoYear)}-${weekEnd.format(weekFormatterWithYear)}"
    } else {
        "${currentWeekStart.format(weekFormatterWithYear)}-${weekEnd.format(weekFormatterWithYear)}"
    }

    SideEffect {
        setTopBar(
            topBarOwnerId,
            TopBarState(
                title = "Schedule",
                onNavigateUp = onBack,
                actions = {
                    TextButton(onClick = { viewModel.toggleViewMode() }) {
                        Text(if (isWeeklyView) "Weekly View" else "Daily View")
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

    fun getCategoryColor(category: String): Color {
        return when (category.lowercase()) {
            "lecture" -> Color(0xFFE63946)
            "exercises" -> Color(0xFF2A9D8F)
            "mid-term" -> Color(0xFF1D3557)
            "final-exam" -> Color(0xFF457B9D)
            "seminary" -> Color(0xFFF4A261)
            "other" -> Color(0xFFBDBDBD)
            else -> Color(0xFFBDBDBD)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isSyncing && events.isNotEmpty()) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            val isInitialLoading = uiState.isSyncing && events.isEmpty()

            if (isInitialLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (isWeeklyView) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = weekLabel,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.previousWeek() },
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                            ) {
                                Text("<")
                            }
                            Button(
                                onClick = { viewModel.nextWeek() },
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                            ) {
                                Text(">")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val daysOfWeek = (0..6).map { currentWeekStart.plusDays(it.toLong()) }

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = true),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(daysOfWeek, key = { it }) { day ->
                            val dayEvents = events.filter { event ->
                                event.date == day || (event.isEveryWeek && event.date.dayOfWeek == day.dayOfWeek)
                            }
                            LazyColumn(
                                modifier = Modifier
                                    .width(150.dp)
                                    .fillMaxHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                item {
                                    Text(
                                        text = day.dayOfWeek.name.take(3),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Divider(Modifier.padding(vertical = 4.dp))
                                }
                                if (dayEvents.isEmpty()) {
                                    item { Text("No events", style = MaterialTheme.typography.bodySmall) }
                                } else {
                                    items(dayEvents, key = { it.id }) { event ->
                                        val categoryColor = getCategoryColor(event.category)
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clickable { viewModel.selectEvent(event) },
                                            elevation = CardDefaults.cardElevation(2.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(IntrinsicSize.Min)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .width(6.dp)
                                                        .fillMaxHeight()
                                                        .background(categoryColor)
                                                )
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(8.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Text(
                                                        event.title,
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textAlign = TextAlign.Center
                                                    )
                                                    Text(
                                                        "${event.startTime} - ${event.endTime}",
                                                        modifier = Modifier.fillMaxWidth(),
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentDay.format(dayFormatter),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.previousDay() },
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                            ) {
                                Text("<")
                            }
                            Button(
                                onClick = { viewModel.nextDay() },
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                            ) {
                                Text(">")
                            }
                        }
                    }

                    val dayEvents = events.filter { event ->
                        event.date == currentDay || (event.isEveryWeek && event.date.dayOfWeek == currentDay.dayOfWeek)
                    }

                    Divider(Modifier.padding(vertical = 16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (dayEvents.isEmpty()) {
                            item { Text("No events today.") }
                        } else {
                            items(dayEvents, key = { it.id }) { event ->
                                val categoryColor = getCategoryColor(event.category)
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.selectEvent(event) },
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(IntrinsicSize.Min)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(6.dp)
                                                .fillMaxHeight()
                                                .background(categoryColor)
                                        )
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                event.title,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Start
                                            )
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${event.startTime} - ${event.endTime}",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    textAlign = TextAlign.Start,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text(
                                                    text = event.location,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    textAlign = TextAlign.End,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                if (!uiState.isSignedOut && !uiState.isSaving) {
                    viewModel.setAddDialogVisible(true)
                }
            },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Event")
        }
    }

    if (showDialog) {
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
                    OutlinedTextField(
                        value = uiState.newStart,
                        onValueChange = viewModel::updateNewStart,
                        label = { Text("Start Time") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.newEnd,
                        onValueChange = viewModel::updateNewEnd,
                        label = { Text("End Time") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.newDateText,
                        onValueChange = { viewModel.updateNewDate(it) },
                        label = { Text("Date (YYYY-MM-DD)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.newLocation,
                        onValueChange = viewModel::updateNewLocation,
                        label = { Text("Location") },
                        singleLine = true
                    )

                    var expandedCategory by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = !expandedCategory }
                    ) {
                        OutlinedTextField(
                            value = if (uiState.newCategory.isBlank()) "Other" else uiState.newCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            categories.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        viewModel.updateNewCategory(option)
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }

                    var expandedEveryWeek by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedEveryWeek,
                        onExpandedChange = { expandedEveryWeek = !expandedEveryWeek }
                    ) {
                        OutlinedTextField(
                            value = if (uiState.newIsEveryWeek) "Yes" else "No",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Repeat Weekly") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEveryWeek)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedEveryWeek,
                            onDismissRequest = { expandedEveryWeek = false }
                        ) {
                            DropdownMenuItem(text = { Text("Yes") }, onClick = {
                                viewModel.updateNewIsEveryWeek(true)
                                expandedEveryWeek = false
                            })
                            DropdownMenuItem(text = { Text("No") }, onClick = {
                                viewModel.updateNewIsEveryWeek(false)
                                expandedEveryWeek = false
                            })
                        }
                    }
                }
            }
        )
    }

    selectedEvent?.let {
        AlertDialog(
            onDismissRequest = { viewModel.dismissSelectedEvent() },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = { viewModel.deleteSelectedEvent() },
                        enabled = !uiState.isSaving
                    ) {
                        Text(
                            "Delete",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    TextButton(
                        onClick = { viewModel.saveEditedEvent() },
                        enabled = !uiState.isSaving
                    ) {
                        Text(if (uiState.isSaving) "Saving..." else "Save Changes")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissSelectedEvent() },
                    enabled = !uiState.isSaving
                ) {
                    Text("Close")
                }
            },
            title = { Text("Event Details") },
            text = {
                val editDialogScroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(editDialogScroll),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.editTitle,
                        onValueChange = viewModel::updateEditTitle,
                        label = { Text("Title") }
                    )
                    OutlinedTextField(
                        value = uiState.editDateText,
                        onValueChange = viewModel::updateEditDate,
                        label = { Text("Date (YYYY-MM-DD)") }
                    )
                    OutlinedTextField(
                        value = uiState.editStart,
                        onValueChange = viewModel::updateEditStart,
                        label = { Text("Start Time") }
                    )
                    OutlinedTextField(
                        value = uiState.editEnd,
                        onValueChange = viewModel::updateEditEnd,
                        label = { Text("End Time") }
                    )
                    OutlinedTextField(
                        value = uiState.editLocation,
                        onValueChange = viewModel::updateEditLocation,
                        label = { Text("Location") }
                    )

                    var expandedEditCategory by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedEditCategory,
                        onExpandedChange = { expandedEditCategory = !expandedEditCategory }
                    ) {
                        OutlinedTextField(
                            value = if (uiState.editCategory.isBlank()) "Other" else uiState.editCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEditCategory)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedEditCategory,
                            onDismissRequest = { expandedEditCategory = false }
                        ) {
                            categories.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        viewModel.updateEditCategory(option)
                                        expandedEditCategory = false
                                    }
                                )
                            }
                        }
                    }

                    var expandedEditEveryWeek by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedEditEveryWeek,
                        onExpandedChange = { expandedEditEveryWeek = !expandedEditEveryWeek }
                    ) {
                        OutlinedTextField(
                            value = if (uiState.editIsEveryWeek) "Yes" else "No",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Repeat Weekly") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEditEveryWeek)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedEditEveryWeek,
                            onDismissRequest = { expandedEditEveryWeek = false }
                        ) {
                            DropdownMenuItem(text = { Text("Yes") }, onClick = {
                                viewModel.updateEditIsEveryWeek(true)
                                expandedEditEveryWeek = false
                            })
                            DropdownMenuItem(text = { Text("No") }, onClick = {
                                viewModel.updateEditIsEveryWeek(false)
                                expandedEditEveryWeek = false
                            })
                        }
                    }
                }
            }
        )
    }
}


