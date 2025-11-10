package com.rampu.erasmapp.schedule.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScheduleScreen(
    onBack: () -> Unit = {}
) {
    var isWeekly by remember { mutableStateOf(true) }

    Scaffold(
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(){
                Text(
                    text = if (isWeekly) "Weekly View" else "Daily View",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.width(24.dp))
                Button(onClick = { isWeekly = !isWeekly }) {
                    Text(if (isWeekly) "Switch to Daily" else "Switch to Weekly")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Placeholder za kasniji sadržaj rasporeda
            Text(
                text = "Your schedule will appear here.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
