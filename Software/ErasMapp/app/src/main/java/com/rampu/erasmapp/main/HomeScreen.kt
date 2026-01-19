package com.rampu.erasmapp.main


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.eventCalendar.data.EventCalendarRepository
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    onSignOut: () -> Unit,
    onGoToSchedule: () -> Unit,
    onGoToEventCalendar: () -> Unit,
    onGoToAdmin: () -> Unit,
    onGoToChannels: () -> Unit,
    onGoToFOI : () -> Unit,
    onGoToNavigation : () -> Unit,
    onGoToNews: () -> Unit
){
    val repository: EventCalendarRepository = koinInject()
    val adminFlow = remember(repository) { repository.observeAdminStatus() }
    val isAdmin by adminFlow.collectAsState(initial = false)

    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .padding(bottom=20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        item { Text("Home screen") }

        item { Spacer(modifier = Modifier.height(20.dp)) }


        item { Spacer(modifier = Modifier.height(20.dp)) }
        item {
            Button(
                onClick = onSignOut
            ){
                Text("Sign out")
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
        item {
            Button(onClick = onGoToSchedule) {
                Text("Go to Schedule")
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
        item {
            Button(onClick = onGoToEventCalendar) {
                Text("Go to Event Calendar")
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
        item {
            Button(
                onClick = onGoToNavigation) {
                Text("Go to Navigation")
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
        item {
            Button(
                onClick = onGoToFOI) {
                Text("FOI Buildings")
            }
        }

        if (isAdmin) {
            item { Spacer(modifier = Modifier.height(20.dp)) }
            item {
                Button(onClick = onGoToAdmin) {
                    Text("Admin console")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
        item{
            Button(onClick = onGoToChannels) {
                Text("Go  to channels")
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
        item{
            Button(onClick = onGoToNews) {
                Text("Go  to news")
            }
        }

    }

}
