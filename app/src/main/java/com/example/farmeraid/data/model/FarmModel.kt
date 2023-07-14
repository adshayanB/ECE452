package com.example.farmeraid.data.model

class FarmModel {
    data class Farm(
        val id : String,
        val name: String,
        val users: List<String>,
        val markets : List<String>,
        val charities: List<String>,
        val transactions: List<String>
    )
}