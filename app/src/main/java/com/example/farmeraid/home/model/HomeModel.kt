package com.example.farmeraid.home.model

import com.example.farmeraid.navigation.NavRoute
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

    enum class TransactionType {
        HARVEST,
        SELL,
        DONATE,
        ALL
    }
    data class Transaction(
        val transactionId: Int,
        val transactionType: TransactionType,
        val produceChanges: MutableMap<String, Int>,
        val locationName: String, // either market name for "SELL" or community fridge name for "DONATE"
        val transactionMessage: String
    )

    sealed class Tab {
        object Quotas : Tab()
        object Inventory : Tab()
    }

    data class HomeViewState(
        val buttonUiState: UiComponentModel.ButtonUiState,
        val inventory: MutableMap<String, Int> = HashMap<String, Int>(),
        val quotasList: List<CategorizedQuotas> = emptyList(),
        val selectedTab: Tab = Tab.Quotas,
    )
}

fun getHomeButton() : UiComponentModel.ButtonUiState {
    return UiComponentModel.ButtonUiState(text = "Test")
}