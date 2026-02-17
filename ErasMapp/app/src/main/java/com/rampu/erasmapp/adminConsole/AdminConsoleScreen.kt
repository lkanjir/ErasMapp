package com.rampu.erasmapp.adminConsole

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminConsoleScreen(
    onManageEvents: () -> Unit,
    onManageRooms: () -> Unit,
    onManageNews: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Admin console",
            fontSize = 36.sp,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.W600
        )

        AdminSectionCard(
            title = "Calendar events",
            description = "Create, edit, or delete calendar events.",
            actionLabel = "Manage",
            onAction = onManageEvents,
            actionEnabled = true
        )
        AdminSectionCard(
            title = "Rooms",
            description = "Manage room listings and availability.",
            actionLabel = "Manage",
            onAction = onManageRooms,
            actionEnabled = true
        )
        AdminSectionCard(
            title = "News content",
            description = "Publish and edit news updates.",
            actionLabel = "Manage",
            onAction = onManageNews,
            actionEnabled = true
        )
    }
}

@Composable
private fun AdminSectionCard(
    title: String,
    description: String,
    actionLabel: String,
    onAction: () -> Unit,
    actionEnabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Button(
                    onClick = onAction,
                    enabled = actionEnabled
                ) {
                    Text(actionLabel)
                }
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
