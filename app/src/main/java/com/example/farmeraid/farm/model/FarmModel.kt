package com.example.farmeraid.farm.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import com.example.farmeraid.data.model.InventoryModel
import com.example.farmeraid.home.model.HomeModel
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.uicomponents.models.UiComponentModel

class FarmModel {

    data class FarmViewState(
        val micFabUiState: UiComponentModel.FabUiState,
        val micFabUiEvent: UiComponentModel.FabUiEvent,
        val submitButtonUiState: UiComponentModel.ButtonUiState,
        val produceHarvestList: List<ProduceHarvest> = emptyList(),
        val speechResult: String
    )

    data class ProduceHarvest(
        val produceName: String,
        val produceCount: Int?,
    )
}

fun getMicButton(): UiComponentModel.FabUiState {
    return UiComponentModel.FabUiState(contentDescription = "Start Listening", icon = Icons.Filled.Mic)
}

fun getStopButton(): UiComponentModel.FabUiState {
    return UiComponentModel.FabUiState(contentDescription = "Stop Listening", icon = Icons.Filled.Stop)
}

fun getSubmitButton(): UiComponentModel.ButtonUiState {
    return UiComponentModel.ButtonUiState(text = "Submit")
}