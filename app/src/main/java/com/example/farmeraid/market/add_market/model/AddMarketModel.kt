package com.example.farmeraid.market.add_market.model

import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.uicomponents.models.UiComponentModel
import java.util.UUID

class AddMarketModel {

    data class ProduceRow(
        val id: UUID = UUID.randomUUID(),
        val produce : String?,
        val quantityPickerUiState: UiComponentModel.QuantityPickerUiState,
    )

    data class AddMarketViewState(
        val marketName: String = "",
        val markets : List<MarketModel.Market> = listOf(),
        val produce : Map<String, Int> = mapOf(),
        val produceRows : List<ProduceRow> = listOf(initializeProduceRow()),
        val submitButtonUiState: UiComponentModel.ButtonUiState,
    )
}

fun getSubmitButton() : UiComponentModel.ButtonUiState {
    return UiComponentModel.ButtonUiState(
        text = "Submit",
    )
}

fun initializeProduceRow() : AddMarketModel.ProduceRow {
    return AddMarketModel.ProduceRow(
        id = UUID.randomUUID(),
        produce = null,
        quantityPickerUiState = UiComponentModel.QuantityPickerUiState(0, null,false)
    )
}