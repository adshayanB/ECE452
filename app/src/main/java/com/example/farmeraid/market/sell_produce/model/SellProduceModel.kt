package com.example.farmeraid.market.sell_produce.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import com.example.farmeraid.data.model.InventoryModel
import com.example.farmeraid.home.model.HomeModel
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.uicomponents.models.UiComponentModel

class SellProduceModel {

    data class SellProduceViewState(
            val submitButtonUiState: UiComponentModel.ButtonUiState,
            val marketName: String = "",
            val produceSellList: List<ProduceSell> = emptyList(),
    )

    data class ProduceSell(
            val produceName: String,
            val produceCount: Int,
            val produceInventory: Int,
            val producePrice: Int,
            val produceTotalPrice: Int,
            val produceQuotaCurrentProgress: Int,
            val produceQuotaTotalGoal: Int
    )
}