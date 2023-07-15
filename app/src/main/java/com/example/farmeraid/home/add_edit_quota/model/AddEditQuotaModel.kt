package com.example.farmeraid.home.add_edit_quota.model

import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.uicomponents.models.UiComponentModel
import java.util.UUID

class AddEditQuotaModel {
    data class ProduceRow(
        val id: UUID = UUID.randomUUID(),
        val produce : String?,
        val quantityPickerUiState: UiComponentModel.QuantityPickerUiState,
    )

    data class AddEditQuotaViewState(
        val markets : List<MarketModel.Market> = listOf(),
        val produce : Map<String, Int> = mapOf(),
        val produceRows : List<ProduceRow> = listOf(initializeProduceRow()),
        val selectedMarket : MarketModel.Market? = null,
        val submitButtonUiState: UiComponentModel.ButtonUiState,
    )
}

fun getSubmitButton() : UiComponentModel.ButtonUiState {
    return UiComponentModel.ButtonUiState(
        text = "Submit",
    )
}

fun initializeProduceRow() : AddEditQuotaModel.ProduceRow {
    return AddEditQuotaModel.ProduceRow(
            id = UUID.randomUUID(),
            produce = null,
            quantityPickerUiState = UiComponentModel.QuantityPickerUiState(0, null,false)
        )
}