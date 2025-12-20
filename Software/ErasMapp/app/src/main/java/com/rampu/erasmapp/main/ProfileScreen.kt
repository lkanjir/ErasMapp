package com.rampu.erasmapp.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.eventCalendar.data.EventCalendarRepository
import org.koin.compose.koinInject

@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    onGoToAdmin: () -> Unit
) {
    val repository: EventCalendarRepository = koinInject()
    val adminFlow = remember(repository) { repository.observeAdminStatus() }
    val isAdmin by adminFlow.collectAsState(initial = false)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile")

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = onSignOut) {
            Text("Sign out")
        }

        if (isAdmin) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onGoToAdmin) {
                Text("Admin controls")
            }
        }
    }
}
