package com.example.farmeraid.charity.model

import com.example.farmeraid.location_provider.LocationProvider
import com.google.maps.android.compose.MapProperties

class CharityModel {
    data class CharityViewState(
        val longitude: Double,
        val latitude: Double,
        val userLocation: LocationProvider.LatandLong,
        val listOfFridges: String = "",
        val properties: MapProperties = MapProperties(),
        val readableLocation: String = "",
    )

    data class LocationCoordinates(
        var longitude : Double,
        var latitude : Double,
    )
}