package com.example.farmeraid.fridge_details.model

import com.example.farmeraid.fridge.model.FridgeModel
import com.example.farmeraid.farm.model.FarmModel
import com.example.farmeraid.uicomponents.models.UiComponentModel

class FridgeDetailsModel {
    data class FridgeDetailViewState(
        val fridgeInventory: List<String> = emptyList(),
        val farmProduces: List<FarmModel.ProduceHarvest> = emptyList(),
        val donateButtonUiState: UiComponentModel.ButtonUiState,
        val fridgeDetails: FridgeModel.Fridge?
    )
}
fun getDonateButton(): UiComponentModel.ButtonUiState {
    return UiComponentModel.ButtonUiState(text = "Donate")
}
