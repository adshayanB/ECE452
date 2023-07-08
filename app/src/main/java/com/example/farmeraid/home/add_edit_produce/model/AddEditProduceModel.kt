package com.example.farmeraid.home.add_edit_produce.model

import com.example.farmeraid.data.model.InventoryModel
import com.example.farmeraid.uicomponents.models.UiComponentModel

class AddEditProduceModel {
    data class AddEditProduceViewState(
        val produceName : String?,
        val produceAmount : Int,
        val submitButtonUiState : UiComponentModel.ButtonUiState,
        val isAddProduce : Boolean,
    )
}

fun getSubmitButton() : UiComponentModel.ButtonUiState {
    return UiComponentModel.ButtonUiState(
        text = "Submit",
    )
}