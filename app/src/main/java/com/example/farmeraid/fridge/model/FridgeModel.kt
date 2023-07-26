package com.example.farmeraid.fridge.model

import com.example.farmeraid.charity.model.CharityModel
import com.example.farmeraid.location_provider.LocationProvider
import com.example.farmeraid.uicomponents.models.UiComponentModel

class FridgeModel {
    data class AddEditFridgeViewState(
        val fridgeName : String? = null,
        val fridgeLocation : String? = null,
        val fridgeHandle: String? = null,
        val submitButtonUiState : UiComponentModel.ButtonUiState,
        val isAddFridge : Boolean = true,
        val fridgeList: List<FridgeDetails> = emptyList(),
    )
    data class Fridge(
        val id: String,
        val fridgeName: String,
        val location: String,
        val imageUri: String,
        val handle:String,
        val coordinates: LocationProvider.LatandLong
    )
    data class FridgeDetails(
        val fridgeProperties: Fridge,
        val distanceFromUser: Float = 0f
    )
}

fun getSubmitButton() : UiComponentModel.ButtonUiState {
    return UiComponentModel.ButtonUiState(
        text = "Submit",
    )
}