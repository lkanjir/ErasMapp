package com.rampu.erasmapp.main

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.rampu.erasmapp.R
import com.rampu.erasmapp.common.ui.components.Logo
import com.rampu.erasmapp.common.ui.components.UserPositionMarker
import kotlinx.coroutines.launch

data class PointOfInterestWithDistance(val poi: PointOfInterest, val distance: Float)

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        val target = LatLng(46.30778948861526, 16.338096828836036)
        position = CameraPosition.fromLatLngZoom(target, 10f)
    }

    val pointsOfInterest = remember {
        listOf(
            PointOfInterest("Eiffel Tower", LatLng(48.8584, 2.2945)),
            PointOfInterest("Louvre Museum", LatLng(48.8606, 2.3376)),
            PointOfInterest("Cathédrale Notre-Dame de Paris", LatLng(48.8529, 2.3500))
        )
    }

    val nearbyPoints by remember(userLocation) {
        derivedStateOf {
            userLocation?.let { loc ->
                val user = Location("").apply {
                    latitude = loc.latitude
                    longitude = loc.longitude
                }
                pointsOfInterest.map {
                    val point = Location("").apply {
                        latitude = it.location.latitude
                        longitude = it.location.longitude
                    }
                    PointOfInterestWithDistance(it, user.distanceTo(point))
                }.sortedBy { it.distance }
            } ?: emptyList()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val userLatLng = LatLng(it.latitude, it.longitude)
                        userLocation = userLatLng
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                com.google.android.gms.maps.CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.fromLatLngZoom(userLatLng, 15f)
                                )
                            )
                        }
                    }
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let { userLocation = LatLng(it.latitude, it.longitude) }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .border(4.dp, MaterialTheme.colorScheme.primary)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                userLocation?.let {
                    UserPositionMarker(position = it)
                }
            }

            Logo(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(48.dp)
            )

            FloatingActionButton(
                onClick = {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) -> {
                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                location?.let {
                                    val userLatLng = LatLng(it.latitude, it.longitude)
                                    userLocation = userLatLng
                                    coroutineScope.launch {
                                        cameraPositionState.animate(
                                            com.google.android.gms.maps.CameraUpdateFactory.newCameraPosition(
                                                CameraPosition.fromLatLngZoom(userLatLng, 15f)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        else -> {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 32.dp, bottom = 32.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_my_location),
                    contentDescription = "Current Location"
                )
            }
        }
        Row(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .border(4.dp, MaterialTheme.colorScheme.primary),
            ) {
                Logo(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .size(48.dp)
                )
                LazyColumn(modifier = Modifier.padding(top = 64.dp)) {
                    item { Text("Close points of interest", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(8.dp)) }
                    if (userLocation == null) {
                        item { Text("Getting location...", modifier = Modifier.padding(16.dp)) }
                    } else {
                        items(nearbyPoints) { (poi, distance) ->
                            Column(modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp)) {
                                Text(poi.name)
                                Text("Distance: %.2f km".format(distance / 1000), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .border(4.dp, MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Logo(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .size(48.dp)
                )
                Text("Recommended locations")
            }
        }
    }
}
