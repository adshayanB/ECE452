package com.example.farmeraid.market.sell_produce.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import com.example.farmeraid.uicomponents.models.UiComponentModel

class SellProduceModel {

    data class SellProduceViewState(
            val submitButtonUiState: UiComponentModel.ButtonUiState,
            val micFabUiState: UiComponentModel.FabUiState,
            val micFabUiEvent: UiComponentModel.FabUiEvent,
            val marketName: String = "",
            val marketId: String = "",
            val produceSellList: List<ProduceSell> = emptyList(),
            val isLoading : Boolean = false,
    )

    data class ProduceSell(
            val produceName: String,
            val produceCount: Int,
            val produceInventory: Int,
            val producePrice: Double,
            val produceTotalPrice: Double,
            val produceQuotaCurrentProgress: Int,
            val produceQuotaTotalGoal: Int
    )

    fun getMicButton(): UiComponentModel.FabUiState {
        return UiComponentModel.FabUiState(contentDescription = "Start Listening", icon = Icons.Filled.Mic)
    }

    fun getStopButton(): UiComponentModel.FabUiState {
        return UiComponentModel.FabUiState(contentDescription = "Stop Listening", icon = Icons.Filled.Stop)
    }
}