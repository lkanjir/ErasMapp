package com.rampu.erasmapp.schedule.ui

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.schedule.data.ScheduleEvent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID



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
                    "12:00"
                ),
                ScheduleEvent("2", "RAMPU", LocalDate.now().with(DayOfWeek.MONDAY), "12:00", "15:00"),
                ScheduleEvent("3", "RPP", LocalDate.now().with(DayOfWeek.TUESDAY), "8:00", "10:00"),
                ScheduleEvent("4", "POP", LocalDate.now().with(DayOfWeek.WEDNESDAY), "12:00", "14:00"),
                ScheduleEvent("5", "RPP", LocalDate.now().with(DayOfWeek.THURSDAY), "17:00", "18:30"),
            )
        )
    }
    /* DIALOG INPUTS */
    var showDialog by remember { mutableStateOf(false)}
    var newTitle by remember { mutableStateOf("") }
    var newDate by remember { mutableStateOf(LocalDate.now()) }
    var newStart by remember { mutableStateOf("") }
    var newEnd by remember { mutableStateOf("") }

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
            Button(onClick = { isWeeklyView = !isWeeklyView }) {
                Text(if (isWeeklyView) "Switch to Daily View" else "Switch to Weekly View")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isWeeklyView) {
                // WEEEKS VIEW
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
                            val dayEvents = demoEvents.filter { it.date == day }
                            if (dayEvents.isEmpty()) {
                                Text("No events", style = MaterialTheme.typography.bodySmall)
                            } else {
                                dayEvents.forEach { event ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        elevation = CardDefaults.cardElevation(2.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(event.title,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center)
                                            Text("${event.startTime}–${event.endTime}",
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center)
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
                val dayEvents = demoEvents.filter { it.date == currentDay }

                Divider(Modifier.padding(vertical = 16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (dayEvents.isEmpty()) {
                        item { Text("No events today.") }
                    } else {
                        items(dayEvents) { event ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(event.title)
                                    Text("${event.startTime}–${event.endTime}")
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
                                    endTime = newEnd
                                )
                            )
                        }
                        newTitle = ""
                        newStart = ""
                        newEnd = ""
                        newDate = LocalDate.now()
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
                }
            }
        )
    }
}
