package com.rampu.erasmapp.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.rampu.erasmapp.R
import com.rampu.erasmapp.common.ui.components.Logo
import com.rampu.erasmapp.common.ui.components.UserPositionMarker
import kotlinx.coroutines.launch



@Composable
fun MapScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var pointsOfInterest by remember { mutableStateOf<List<PointOfInterest>>(emptyList()) }

    val recommendedPlaces = remember {
        listOf(
            RecommendedPlace("FOI University", LatLng(46.3077024, 16.3355112)),
            RecommendedPlace("Strauss Club", LatLng(46.3092458, 16.3335036)),
            RecommendedPlace("Spar", LatLng(46.3156265, 16.3476229))
        )
    }

    val recommendedPointsWithDistance by remember(userLocation) {
        derivedStateOf {
            userLocation?.let { loc ->
                val user = Location("").apply {
                    latitude = loc.latitude
                    longitude = loc.longitude
                }
                recommendedPlaces.map {
                    val point = Location("").apply {
                        latitude = it.location.latitude
                        longitude = it.location.longitude
                    }
                    RecommendedPlaceWithDistance(it, user.distanceTo(point))
                }
            } ?: recommendedPlaces.map { RecommendedPlaceWithDistance(it, 0f) }
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        val target = LatLng(46.30778948861526, 16.338096828836036)
        position = CameraPosition.fromLatLngZoom(target, 15f)
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
                                CameraUpdateFactory.newCameraPosition(
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

    LaunchedEffect(userLocation) {
        userLocation?.let { userLatLng ->
            val placesClient = Places.createClient(context)
            val placeFields = listOf(Place.Field.DISPLAY_NAME, Place.Field.LOCATION)
            val locRestriction = CircularBounds.newInstance(userLatLng, 1000.0)

            val request = SearchNearbyRequest.builder(locRestriction, placeFields)
                .setIncludedTypes(listOf("restaurant", "bar", "night_club"))
                .setMaxResultCount(20)
                .build()

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                placesClient.searchNearby(request)
                    .addOnSuccessListener { response ->
                        Log.d("MapScreen", "Response received")
                        pointsOfInterest = response.places.mapNotNull { place ->
                            place.location?.let {
                                PointOfInterest(place.displayName ?: "Unknown", it)
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("MapScreen", "Error searching for places", exception)
                    }
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
                recommendedPlaces.forEach { place ->
                    val icon = when (place.name) {
                        "FOI University" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                        "Strauss Club" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
                        "Spar" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                        else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    }
                    Marker(
                        state = rememberUpdatedMarkerState(position = place.location),
                        title = place.name,
                        icon = icon
                    )
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
                                            CameraUpdateFactory.newCameraPosition(
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
                contentAlignment = Alignment.TopCenter
            ) {
                Logo(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .size(48.dp)
                )
                LazyColumn(modifier = Modifier.padding(top = 64.dp)) {
                    item { Text("Recommended locations", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(8.dp)) }
                    items(recommendedPointsWithDistance) { (place, distance) ->
                        Column(modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp)) {
                            Text(place.name)
                            if (userLocation != null) {
                                Text(
                                    text = "Distance: %.2f km".format(distance / 1000),
                                    style = MaterialTheme.typography.bodySmall
                                )
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
}
