package com.example.farmeraid.sign_in.model

import com.example.farmeraid.home.model.HomeModel
import com.example.farmeraid.uicomponents.models.UiComponentModel

class SignInModel {

    data class SignInViewState(
        val buttonUiState: UiComponentModel.ButtonUiState,
        val userName: String = "",
        val passWord: String = "",
        val loggedIn: String = "",
    )
}

fun getSignInButton() : UiComponentModel.ButtonUiState {
    return UiComponentModel.ButtonUiState(text = "Test")
}