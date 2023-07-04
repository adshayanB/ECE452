package com.example.farmeraid.data.model

class UserModel {
    data class User(
        val email: String,
        val id: String,
        val farm_id: String
    )
}