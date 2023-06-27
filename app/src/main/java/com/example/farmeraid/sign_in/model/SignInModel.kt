package com.example.farmeraid.sign_in.model

import com.example.farmeraid.uicomponents.models.UiComponentModel

class SignInModel {
    sealed class AuthResponse {
        object Success : AuthResponse()
        data class Error(val error: String) : AuthResponse()
    }
    data class SignInViewState(
        val buttonUiState: UiComponentModel.ButtonUiState,
        val userName: String = "",
        val passWord: String = "",
        val isLoading: Boolean = false,

    )
}

fun getSignInButton() : UiComponentModel.ButtonUiState {
    return UiComponentModel.ButtonUiState(text = "Sign In")
}