package com.example.farmeraid.data.model

class MarketModel {
    data class Market(
        val name: String,
    ) {
        override fun toString(): String = name
    }
}