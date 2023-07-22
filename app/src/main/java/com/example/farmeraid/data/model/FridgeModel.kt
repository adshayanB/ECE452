package com.example.farmeraid.data.model

import com.example.farmeraid.data.CharityRepository

class FridgeModel {
    data class Fridge(
        val fridgeName: String,
        val location: String,
        val items: List<CharityModel.ProduceFridge>
    )
}