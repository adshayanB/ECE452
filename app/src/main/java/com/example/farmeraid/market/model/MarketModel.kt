package com.example.farmeraid.market.model

import com.example.farmeraid.data.model.MarketModel

class MarketPageModel {

    data class MarketViewState(
        val marketList: List<MarketModel.Market> = emptyList(),
        val isLoading: Boolean = false,
    )
}