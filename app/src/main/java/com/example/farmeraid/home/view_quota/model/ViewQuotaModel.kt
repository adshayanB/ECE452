package com.example.farmeraid.home.view_quota.model

import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.data.model.MarketModel

class ViewQuotaModel {
    data class ViewQuotaViewState(
        val quota : MarketModel.MarketWithQuota? = null,
    )
}