package com.example.farmeraid.sign_up.model

import com.example.farmeraid.uicomponents.models.UiComponentModel

class SignUpModel {
    sealed class AuthResponse {
        object Success : AuthResponse()
        data class Error(val error: String) : AuthResponse()
    }
    data class SignUpViewState(
        val buttonUiState: UiComponentModel.ButtonUiState,
        val name: String = "",
        val userName: String = "",
        val passWord: String = "",
        val loggedIn: String = "",
        val isLoading: Boolean = false,

        )
}

fun getSignUpButton() : UiComponentModel.ButtonUiState {
    return UiComponentModel.ButtonUiState(text = "SignUp")
}