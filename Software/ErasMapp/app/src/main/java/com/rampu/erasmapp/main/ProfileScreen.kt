package com.rampu.erasmapp.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.R
import com.rampu.erasmapp.eventCalendar.data.EventCalendarRepository
import com.rampu.erasmapp.ui.theme.ErasMappTheme
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
        Row(
            modifier = Modifier.clickable(onClick = onSignOut),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.logout), contentDescription = "logout")
            Text("Logout")
        }

        if (isAdmin) {
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(onClick = onGoToAdmin, modifier = Modifier.padding(10.dp)) {
                Text("Admin controls")
            }
        }
    }
}

@Composable
@Preview()
fun ProfileScreenPreview() {
    ErasMappTheme {
        ProfileScreen(
            onSignOut = {}
        ) { }
    }
}