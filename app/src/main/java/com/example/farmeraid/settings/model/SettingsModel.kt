package com.example.farmeraid.settings.model

import com.example.farmeraid.uicomponents.models.UiComponentModel

class SettingsModel {
    data class SettingsViewState(
        val buttonUiState: UiComponentModel.ButtonUiState,
        val code: String = "",
    )
}

fun getSignOutButton() : UiComponentModel.ButtonUiState {
    return UiComponentModel.ButtonUiState(text = "Sign Out")
}