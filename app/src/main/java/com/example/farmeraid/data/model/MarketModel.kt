package com.example.farmeraid.data.model

import com.example.farmeraid.data.QuotasRepository

class MarketModel {
    data class Market(
        val id : String,
        val name: String,
        val prices: MutableMap<String, Int>,
    ) {
        override fun toString(): String = name
    }

    data class MarketWithQuota(
        val id : String,
        val name : String,
        val prices: MutableMap<String, Int>,
        val quota : QuotasRepository.Quota,
    ) {
        override fun toString(): String = name
    }
}