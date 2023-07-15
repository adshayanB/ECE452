package com.example.farmeraid.market.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import com.example.farmeraid.data.model.InventoryModel
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.home.model.HomeModel
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.uicomponents.models.UiComponentModel

class MarketPageModel {

    data class MarketViewState(
        val marketWithQuotaList: List<MarketModel.MarketWithQuota> = emptyList(),
        val isLoading: Boolean = false,
    )
}