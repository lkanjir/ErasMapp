package com.rampu.erasmapp.navigation

import com.google.android.gms.maps.model.LatLng

data class PointOfInterest(
    val name: String,
    val location: LatLng
)

data class PointOfInterestWithDistance(
    val poi: PointOfInterest,
    val distance: Float
)
