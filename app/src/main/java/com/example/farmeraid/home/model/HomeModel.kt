package com.example.farmeraid.home.model

import com.example.farmeraid.uicomponents.models.UiComponentModel

class HomeModel {
    data class Produce(
        val produceName: String,
        val produceAmount: Int,
    )

    data class Quota(
        val produceName: String,
        val produceSoldAmount: Int,
        val produceGoalAmount: Int,
    )

    data class CategorizedQuotas(
        val marketName: String,
        val quotas: List<Quota>,
    )

    sealed class Tab {
        object Quotas : Tab()
        object Inventory : Tab()
    }

    data class HomeViewState(
        val buttonUiState: UiComponentModel.ButtonUiState,
        val inventoryList: List<Produce> = emptyList(),
        val quotasList: List<CategorizedQuotas> = emptyList(),
        val selectedTab: Tab = Tab.Quotas,
    )
}

fun getHomeButton() : UiComponentModel.ButtonUiState {
    return UiComponentModel.ButtonUiState(text = "Test")
}