package com.example.farmeraid.data.model

import com.example.farmeraid.location_provider.LocationProvider

class FridgeModel {
    data class Fridge(
        val id: String,
        val fridgeName: String,
        val location: String,
        val items: List<CharityModel.ProduceFridge>,
        val coordinates: LocationProvider.LatandLong
    )
}