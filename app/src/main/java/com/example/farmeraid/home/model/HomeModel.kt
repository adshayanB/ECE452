package com.example.farmeraid.home.model
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.data.model.InventoryModel
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.uicomponents.models.UiComponentModel

class HomeModel {
    sealed class Tab(val index : Int, val name : String) {
        object Quotas : Tab(0, "Weekly Quotas")
        object Inventory : Tab(1, "Inventory")
    }

    data class HomeViewState(
        val inventoryList: MutableMap<String, Int> = mutableMapOf<String, Int>(),
        val quotasList: List<MarketModel.MarketWithQuota> = emptyList(),
        val selectedTab: Tab = Tab.Quotas,
        val isLoading: Boolean = false,
    )
}