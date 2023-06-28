package com.example.farmeraid.home.model

import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.uicomponents.models.UiComponentModel

class HomeModel {
    data class Produce(
        val produceName: String,
        val produceAmount: Int,
    )

    data class ProduceQuota(
        val produceName : String,
        val produceSoldAmount : Int,
        val produceGoalAmount : Int,
    )

    data class Quota(
        val marketName : String,
        val produceQuotaList : List<ProduceQuota>,
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

    sealed class Tab(val index : Int, val name : String) {
        object Quotas : Tab(0, "Quotas")
        object Inventory : Tab(1, "Inventory")
    }

    data class HomeViewState(
        val inventory: MutableMap<String, Int> = HashMap<String, Int>(),
        val quotasList: List<Quota> = emptyList(),
        val selectedTab: Tab = Tab.Quotas,
    )
}