package com.example.farmeraid.sign_in.model

class LoadingModel {
    sealed class AuthResponse {
        object Success : AuthResponse()
        data class Error(val error: String) : AuthResponse()
    }
}
