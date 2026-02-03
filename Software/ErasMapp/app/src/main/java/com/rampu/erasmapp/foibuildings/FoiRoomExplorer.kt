package com.rampu.erasmapp.foibuildings

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.collections.chunked
import kotlin.collections.forEach
public val foi1Floors = listOf(
    Floor("2nd Floor ", listOf(Room("D1"), Room("D2"), Room("D8"), Room("D3"))),
    Floor("1st Floor", listOf(Room("D4"), Room("D11"), Room("Dekanat"))),
    Floor("Ground Floor", listOf(Room("Lab 5"), Room("D10"), Room("D6"), Room("D7"), Room("Fotokopiraonica"))),
    Floor("Basement", listOf(Room("Cafe"), Room("D9"), Room("Knjižnica")))
)

@Composable
public fun Foi1RoomExplorer(onBack: () -> Unit) {
    var selectedRoom by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to buildings")
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(foi1Floors) { floor ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = floor.title, style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        floor.rooms.chunked(3).forEach { rowRooms ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                                rowRooms.forEach { room ->
                                    Button(onClick = { selectedRoom = "${floor.title} - ${room.name}" }) {
                                        Text(room.name)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        selectedRoom?.let {
            Text(
                text = "Selected: $it",
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}