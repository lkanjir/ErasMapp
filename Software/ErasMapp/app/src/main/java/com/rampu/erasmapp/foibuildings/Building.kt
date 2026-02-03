package com.rampu.erasmapp.foibuildings

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes

data class Building(
    @DrawableRes val imageRes: Int,
    val id: Int,
    @RawRes val descriptionRes: Int,
    val latitude: Double,
    val longitude: Double
)

public data class Room(
    val name: String
)
public data class Floor(
    val title: String,
    val rooms: List<Room>
)