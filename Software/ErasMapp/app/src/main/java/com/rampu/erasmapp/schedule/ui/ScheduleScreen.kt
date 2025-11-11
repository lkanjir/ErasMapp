package com.rampu.erasmapp.schedule.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rampu.erasmapp.schedule.data.ScheduleEvent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onBack: () -> Unit = {}
) {
    /* DEMO DATA */
    var demoEvents by remember {
        mutableStateOf(
            mutableListOf(
                ScheduleEvent(
                    "1",
                    "RWA",
                    LocalDate.now().with(DayOfWeek.MONDAY),
                    "10:00",
                    "12:00",
                    location = "Room 3, FOI1",
                    category = "Lecture",
                    isEveryWeek = true
                ),
                ScheduleEvent("2", "RAMPU", LocalDate.now().with(DayOfWeek.MONDAY), "12:00", "15:00", location = "Room 3, FOI1", category = "Lecture", isEveryWeek = true),
                ScheduleEvent("3", "RPP", LocalDate.now().with(DayOfWeek.TUESDAY), "8:00", "10:00", location = "Room 3, FOI2", category = "Lecture", isEveryWeek = true),
                ScheduleEvent("4", "RAMPU", LocalDate.now().with(DayOfWeek.TUESDAY), "12:00", "18:00", location = "-", category = "Other", isEveryWeek = false),
                ScheduleEvent("5", "POP", LocalDate.now().with(DayOfWeek.WEDNESDAY), "12:00", "14:00", location = "Room 7, FOI1", category = "Lecture", isEveryWeek = true),
                ScheduleEvent("6", "POP", LocalDate.now().with(DayOfWeek.WEDNESDAY), "12:00", "14:00", location = "Room 7, FOI1", category = "Mid-Term", isEveryWeek = false),
                ScheduleEvent("7", "RPP", LocalDate.now().with(DayOfWeek.THURSDAY), "17:00", "18:30", location = "Room 4, FOI1", category = "Exercises", isEveryWeek = true),
            )
        )
    }
    val categories = listOf("Lecture", "Exercises", "Mid-Term", "Final-Exam", "Seminary", "Other")

    /* FAB DIALOG */
    var showDialog by remember { mutableStateOf(false)}
    var newTitle by remember { mutableStateOf("") }
    var newDate by remember { mutableStateOf(LocalDate.now()) }
    var newStart by remember { mutableStateOf("") }
    var newEnd by remember { mutableStateOf("") }
    var newLocation by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("") }
    var newIsEveryWeek by remember { mutableStateOf(false) }

    /* EVENT DETAILS DIALOG */
    var selectedEvent by remember { mutableStateOf<ScheduleEvent?>(null)}
    var editTitle by remember {mutableStateOf("")}
    var editDateText by remember { mutableStateOf("") }
    var editStart by remember { mutableStateOf("") }
    var editEnd by remember { mutableStateOf("") }
    var editLocation by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf("") }
    var editIsEveryWeek by remember { mutableStateOf(false) }

    /* DATES */
    var currentDay by remember { mutableStateOf(LocalDate.now()) }
    var currentWeekStart by remember { mutableStateOf(LocalDate.now().with(DayOfWeek.MONDAY)) }
    var isWeeklyView by remember { mutableStateOf(true) }
    //FORMATTERS
    val dayFormatter = remember { DateTimeFormatter.ofPattern("EEEE, d.MM.yyyy", Locale.getDefault()) }
    val weekFormatterNoYear = remember { DateTimeFormatter.ofPattern("d.MM", Locale.getDefault()) }
    val weekFormatterWithYear = remember { DateTimeFormatter.ofPattern("d.MM.yyyy", Locale.getDefault()) }


    val weekEnd = currentWeekStart.plusDays(6)
    val weekLabel = if (currentWeekStart.year == weekEnd.year) {
        "${currentWeekStart.format(weekFormatterNoYear)}–${weekEnd.format(weekFormatterWithYear)}"
    } else {
        "${currentWeekStart.format(weekFormatterWithYear)}–${weekEnd.format(weekFormatterWithYear)}"
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

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {showDialog = true},
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        }, floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ){
                Text("Schedule",
                    fontSize = 42.sp,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.W600)
                Button(onClick = { isWeeklyView = !isWeeklyView }) {
                    Text(if (isWeeklyView) "Weekly View" else "Daily View")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isWeeklyView) {
                // WEEKS VIEW
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
                            onClick = { currentWeekStart = currentWeekStart.minusWeeks(1) },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                        ) {
                            Text("<")
                        }
                        Button(
                            onClick = { currentWeekStart = currentWeekStart.plusWeeks(1) },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                        ) {
                            Text(">")
                        }
                    }
                }

                val daysOfWeek = (0..6).map { currentWeekStart.plusDays(it.toLong()) }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(daysOfWeek) { day ->
                        Column(
                            modifier = Modifier.width(140.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = day.dayOfWeek.name.take(3),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Divider(Modifier.padding(vertical = 4.dp))

                            val dayEvents = demoEvents.filter { event -> event.date == day || (event.isEveryWeek && event.date.dayOfWeek == day.dayOfWeek) }

                            if (dayEvents.isEmpty()) {
                                Text("No events", style = MaterialTheme.typography.bodySmall)
                            } else {
                                dayEvents.forEach { event ->
                                    val categoryColor = getCategoryColor(event.category)
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable{
                                              selectedEvent = event
                                                editTitle = event.title
                                                editDateText = event.date.toString()
                                                editStart = event.startTime
                                                editEnd = event.endTime
                                                editLocation = event.location
                                                editCategory = event.category
                                                editIsEveryWeek = event.isEveryWeek
                                            },
                                            elevation = CardDefaults.cardElevation(2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(IntrinsicSize.Min) // FOR CATEGORYCOLOR
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
                                                    "${event.startTime}–${event.endTime}",
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
            } else {
                // DAYS VIEW
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
                            onClick = { currentDay = currentDay.minusDays(1) },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                        ) {
                            Text("<")
                        }
                        Button(
                            onClick = { currentDay = currentDay.plusDays(1) },
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                        ) {
                            Text(">")
                        }
                    }

                }
                val dayEvents = demoEvents.filter { event -> event.date == currentDay || (event.isEveryWeek && event.date.dayOfWeek == currentDay.dayOfWeek) }

                Divider(Modifier.padding(vertical = 16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (dayEvents.isEmpty()) {
                        item { Text("No events today.") }
                    } else {
                        items(dayEvents) { event ->
                            val categoryColor = getCategoryColor(event.category)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable{
                                        selectedEvent = event
                                        editTitle = event.title
                                        editDateText = event.date.toString()
                                        editStart = event.startTime
                                        editEnd = event.endTime
                                        editLocation = event.location
                                        editCategory = event.category
                                        editIsEveryWeek = event.isEveryWeek
                                    },
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
                                                text = "${event.startTime}–${event.endTime}",
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
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    if (newTitle.isNotBlank() && newStart.isNotBlank() && newEnd.isNotBlank()) {
                        demoEvents = demoEvents.toMutableList().apply {
                            add(
                                ScheduleEvent(
                                    id = UUID.randomUUID().toString(),
                                    title = newTitle,
                                    date = newDate,
                                    startTime = newStart,
                                    endTime = newEnd,
                                    location = newLocation.ifBlank { "-" },
                                    category = newCategory.ifBlank { "Other" },
                                    isEveryWeek = newIsEveryWeek
                                )
                            )
                        }
                        newTitle = ""
                        newStart = ""
                        newEnd = ""
                        newDate = LocalDate.now()
                        newLocation = ""
                        newCategory = ""
                        newIsEveryWeek = false
                        showDialog = false
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            },
            title = { Text("Add New Event") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Title") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newStart,
                        onValueChange = { newStart = it },
                        label = { Text("Start Time") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newEnd,
                        onValueChange = { newEnd = it },
                        label = { Text("End Time") },
                        singleLine = true
                    )

                    var newDate by remember { mutableStateOf(LocalDate.now()) }
                    var newDateText by remember { mutableStateOf(newDate.toString()) }
                    OutlinedTextField(
                        value = newDateText,
                        onValueChange = { input ->
                            newDateText = input
                            runCatching {
                                newDate = LocalDate.parse(input)
                            }
                        },
                        label = { Text("Date (YYYY-MM-DD)") },
                        singleLine = true
                    )
                    OutlinedTextField(value = newLocation, onValueChange = { newLocation = it }, label = { Text("Location") })
                    //DROPDOWN
                    var expandedCategory by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = !expandedCategory }
                    ) {
                        OutlinedTextField(
                            value = if (newCategory.isBlank()) "Other" else newCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                            },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            categories.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        newCategory = option
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
                            value = if (newIsEveryWeek) "Yes" else "No",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Repeat Weekly") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEveryWeek)
                            },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedEveryWeek,
                            onDismissRequest = { expandedEveryWeek = false }
                        ) {
                            DropdownMenuItem(text = { Text("Yes") }, onClick = {
                                newIsEveryWeek = true
                                expandedEveryWeek = false
                            })
                            DropdownMenuItem(text = { Text("No") }, onClick = {
                                newIsEveryWeek = false
                                expandedEveryWeek = false
                            })
                        }
                    }
                }
            }
        )
    }

    selectedEvent?.let{event ->
        AlertDialog(
            onDismissRequest = {selectedEvent = null},
            confirmButton = {
                TextButton(onClick = {
                    runCatching {
                        val date = LocalDate.parse(editDateText)
                        event.title = editTitle
                        event.date = date
                        event.startTime = editStart
                        event.endTime = editEnd
                        event.location = editLocation
                        event.category = editCategory
                        event.isEveryWeek = editIsEveryWeek
                        demoEvents = demoEvents.toMutableList()
                        selectedEvent = null
                    }
                }){Text("Save Changes")}
            },
            dismissButton = {
                TextButton(onClick = {selectedEvent = null}) {
                    Text("Close")
                }
            },
            title = {Text("Event Details")},
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = {editTitle = it},
                        label = {Text("Title")}
                    )
                    OutlinedTextField(
                        value = editStart,
                        onValueChange = {editStart = it},
                        label = {Text("Start Time")}
                    )
                    OutlinedTextField(
                        value = editEnd,
                        onValueChange = {editEnd = it},
                        label = {Text("End Time")}
                    )
                    //DROPDOWN
                    var expandedEditCategory by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedEditCategory,
                        onExpandedChange = { expandedEditCategory = !expandedEditCategory }
                    ) {
                        OutlinedTextField(
                            value = if (editCategory.isBlank()) "Other" else editCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEditCategory)
                            },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedEditCategory,
                            onDismissRequest = { expandedEditCategory = false }
                        ) {
                            categories.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        editCategory = option
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
                            value = if (editIsEveryWeek) "Yes" else "No",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Repeat Weekly") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEditEveryWeek)
                            },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedEditEveryWeek,
                            onDismissRequest = { expandedEditEveryWeek = false }
                        ) {
                            DropdownMenuItem(text = { Text("Yes") }, onClick = {
                                editIsEveryWeek = true
                                expandedEditEveryWeek = false
                            })
                            DropdownMenuItem(text = { Text("No") }, onClick = {
                                editIsEveryWeek = false
                                expandedEditEveryWeek = false
                            })
                        }
                    }
                }
            }
        )
    }
}
