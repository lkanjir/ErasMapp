package com.rampu.erasmapp.foibuildings

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.R
import java.util.Locale


private val buildings = listOf(
    Building(R.drawable.foi1, 1, R.raw.building_foi1, 46.30771134049019, 16.33798053554669),
    Building(R.drawable.foi2, 2, R.raw.building_foi2, 46.3093296, 16.3417354),
    Building(R.drawable.foi3, 3, R.raw.building_foi3, 46.3083729, 16.3407763)
)

@Composable
fun BuildingScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedBuilding by remember { mutableStateOf<Int?>(null) }
    var showFoi1Rooms by remember { mutableStateOf(false) }

    BackHandler(enabled = selectedBuilding != null || showFoi1Rooms) {
        if (showFoi1Rooms) {
            showFoi1Rooms = false
        } else {
            selectedBuilding = null
        }
    }

    if (showFoi1Rooms) {
        Foi1RoomExplorer(onBack = { showFoi1Rooms = false })
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            buildings.forEach { building ->
                AnimatedVisibility(
                    visible = selectedBuilding == null || selectedBuilding == building.id,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.clickable {
                            selectedBuilding = if (selectedBuilding == building.id) null else building.id
                        },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = building.imageRes),
                            contentDescription = "FOI ${building.id}"
                        )
                        AnimatedVisibility(
                            visible = selectedBuilding == building.id,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val description = context.resources
                                    .openRawResource(building.descriptionRes)
                                    .bufferedReader()
                                    .use { it.readText() }
                                Text(
                                    text = description,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Button(
                                    onClick = {
                                        val uri = String.format(
                                            Locale.ENGLISH,
                                            "google.navigation:q=%f,%f",
                                            building.latitude,
                                            building.longitude
                                        )
                                        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri))
                                        context.startActivity(intent)
                                    }
                                ) {
                                    Text(text = "Show Itinerary")
                                }
                                if (building.id == 1) {
                                    Button(onClick = { showFoi1Rooms = true }) {
                                        Text(text = "Show Rooms")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    }
}
