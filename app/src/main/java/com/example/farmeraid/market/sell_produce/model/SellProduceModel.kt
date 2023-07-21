package com.example.farmeraid.market.sell_produce.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import com.example.farmeraid.data.model.InventoryModel
import com.example.farmeraid.home.model.HomeModel
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.uicomponents.models.UiComponentModel
import kotlinx.coroutines.flow.MutableStateFlow

class SellProduceModel {

    data class SellProduceViewState(
            val submitButtonUiState: UiComponentModel.ButtonUiState,
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
}