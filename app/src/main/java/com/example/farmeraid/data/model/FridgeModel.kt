package com.example.farmeraid.data.model

import com.example.farmeraid.data.QuotasRepository

class FridgeModel {
    data class Fridge(
        val fridgeName: String,
        val location: String,
        val items: List<QuotasRepository.ProduceQuota>
    )
}