package com.example.farmeraid.market.add_edit_market.model

import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.uicomponents.models.UiComponentModel
import java.util.UUID

class AddEditMarketModel {

    data class ProduceRow(
        val id: UUID = UUID.randomUUID(),
        val produce : String?,
        val quantityPickerUiState: UiComponentModel.QuantityPickerUiState,
    )

    data class AddEditMarketViewState(
        val marketName: String = "",
        val selectedMarket : MarketModel.Market? = null,
        val markets : List<MarketModel.Market> = listOf(),
        val produce : Map<String, Int> = mapOf(),
        val produceRows : List<ProduceRow> = listOf(initializeProduceRow()),
        val submitButtonUiState: UiComponentModel.ButtonUiState,
        val isLoading: Boolean = false,
    )
}

fun getSubmitButton() : UiComponentModel.ButtonUiState {
    return UiComponentModel.ButtonUiState(
        text = "Submit",
    )
}

fun initializeProduceRow() : AddEditMarketModel.ProduceRow {
    return AddEditMarketModel.ProduceRow(
        id = UUID.randomUUID(),
        produce = null,
        quantityPickerUiState = UiComponentModel.QuantityPickerUiState(0, null,false)
    )
}