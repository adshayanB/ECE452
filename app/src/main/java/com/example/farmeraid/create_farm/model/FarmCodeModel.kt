package com.example.farmeraid.create_farm.model

import com.example.farmeraid.uicomponents.models.UiComponentModel

class FarmCodeModel {
    data class FarmCodeViewState(
        val buttonUiState: UiComponentModel.ButtonUiState,
        val isLoading : Boolean = false,
    )
}

fun getStartButton() : UiComponentModel.ButtonUiState {
    return UiComponentModel.ButtonUiState(text = "Let's Start")
}