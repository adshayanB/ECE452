package com.example.farmeraid.data.model

class FridgeModel {
    data class Fridge(
        val fridgeName: String,
        val location: String,
        val items: List<CharityModel.ProduceFridge>
    )
}