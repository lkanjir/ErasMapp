package com.rampu.erasmapp.main

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
                    .padding(end = 55.dp, bottom = 10.dp)
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
                contentAlignment = Alignment.Center
            ) {
                Logo(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .size(48.dp)
                )
                Text("Close points of interests")
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
