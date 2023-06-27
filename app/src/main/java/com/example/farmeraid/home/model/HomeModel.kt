package com.example.farmeraid.home.model
import com.example.farmeraid.data.model.InventoryModel
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.uicomponents.models.UiComponentModel

class HomeModel {
    sealed class Tab(val index : Int, val name : String) {
        object Quotas : Tab(0, "Quotas")
        object Inventory : Tab(1, "Inventory")
    }

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
        val inventoryList: List<InventoryModel.Produce> = emptyList(),
        val quotasList: List<CategorizedQuotas> = emptyList(),
        val selectedTab: Tab = Tab.Quotas,
    )
}