package com.example.farmeraid.home.add_edit_produce.model

import com.example.farmeraid.uicomponents.models.UiComponentModel

class AddEditProduceModel {
    data class AddEditProduceViewState(
        val produceName : String? = null,
        val produceAmount : Int? = null,
        val submitButtonUiState : UiComponentModel.ButtonUiState,
        val isAddProduce : Boolean = true,
    )
}

fun getSubmitButton() : UiComponentModel.ButtonUiState {
    return UiComponentModel.ButtonUiState(
        text = "Submit",
    )
}