package com.example.farmeraid.uicomponents.models

class UiComponentModel {
    data class ButtonUiState(
        val text: String,
        val enabled: Boolean = true,
    )

    data class ButtonUiEvent(
        val onClick: () -> Unit = {},
    )
}