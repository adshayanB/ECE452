package com.example.farmeraid.home.model

import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.uicomponents.models.UiComponentModel

class HomeModel {
    sealed class Tab(val index : Int, val name : String) {
        object Quotas : Tab(0, "Quotas")
        object Inventory : Tab(1, "Inventory")
    }

    data class HomeViewState(
        val inventory: MutableMap<String, Int> = HashMap<String, Int>(),
        val quotasList: List<QuotasRepository.Quota> = emptyList(),
        val selectedTab: Tab = Tab.Quotas,
    )
}