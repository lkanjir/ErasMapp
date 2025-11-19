package com.rampu.erasmapp.common.ui.components

import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberUpdatedMarkerState

@Composable
fun UserPositionMarker(
    position: LatLng,
    title: String = "Your Location"
) {
    Marker(
        state = rememberUpdatedMarkerState(position = position),
        title = title
    )
}
