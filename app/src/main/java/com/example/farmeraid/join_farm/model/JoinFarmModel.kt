package com.example.farmeraid.join_farm.model

import com.example.farmeraid.uicomponents.models.UiComponentModel

class JoinFarmModel {
    data class JoinFarmViewState(
        val buttonUiState: UiComponentModel.ButtonUiState,
        val farmCode: String = "",
    )
}

fun getSubmitButton() : UiComponentModel.ButtonUiState {
    return UiComponentModel.ButtonUiState(text = "Join")
}