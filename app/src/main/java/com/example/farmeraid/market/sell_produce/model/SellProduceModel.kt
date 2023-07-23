package com.example.farmeraid.market.sell_produce.model

import com.example.farmeraid.uicomponents.models.UiComponentModel

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