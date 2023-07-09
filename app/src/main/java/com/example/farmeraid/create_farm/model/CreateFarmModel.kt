package com.example.farmeraid.create_farm.model

import com.example.farmeraid.uicomponents.models.UiComponentModel

class CreateFarmModel {
    data class CreateFarmViewState(
        val buttonUiState: UiComponentModel.ButtonUiState,
        val farmName: String = "",
        val location: String = "",
    )
}

fun getSubmitButton() : UiComponentModel.ButtonUiState {
    return UiComponentModel.ButtonUiState(text = "Next")
}