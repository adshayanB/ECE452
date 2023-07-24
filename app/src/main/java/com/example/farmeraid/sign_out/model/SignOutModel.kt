package com.example.farmeraid.sign_up.model

class SignOutModel {
    sealed class AuthResponse {
        object Success : AuthResponse()
        data class Error(val error: String) : AuthResponse()
    }
    data class SignOutViewState(
        val userName: String = "",
        val passWord: String = "",
        val isLoading: Boolean = false,
    )
}

