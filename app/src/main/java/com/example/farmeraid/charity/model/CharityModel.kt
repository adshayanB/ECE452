package com.example.farmeraid.charity.model

import com.example.farmeraid.uicomponents.models.UiComponentModel
import com.google.maps.android.compose.MapProperties

class CharityModel {
    data class CharityViewState(
        val longitude: Double,
        val latitude: Double,
        val listOfFridges: String = "",
        val properties : MapProperties = MapProperties(),
        val readableLocation: String = "",
    )

    data class LocationCoordinates(
        val longitude : Double,
        val latitude : Double,
    )
}