package com.rampu.erasmapp.navigation

import com.google.android.gms.maps.model.LatLng

data class RecommendedPlace(
    val name: String,
    val location: LatLng
)

data class RecommendedPlaceWithDistance(
    val place: RecommendedPlace,
    val distance: Float
)