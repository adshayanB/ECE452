package com.example.farmeraid.charity.model

import com.example.farmeraid.data.model.FridgeModel
import com.example.farmeraid.location_provider.LocationProvider
import com.google.maps.android.compose.MapProperties

class CharityModel {
    data class CharityViewState(
        val longitude: Double,
        val latitude: Double,
        val userLocation: LocationProvider.LatandLong,
        val fridgeList: List<FridgeDetails> = emptyList(),
        val readableLocation: String = "",
    )
    data class FridgeDetails(
        val fridgeProperties: FridgeModel.Fridge,
        val distanceFromUser: Float = 0f
    )
}