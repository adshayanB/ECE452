package com.example.farmeraid.data

import com.example.farmeraid.data.model.MarketModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MarketRepository {
    private val markets: MutableList<MarketModel.Market> = mutableListOf(
        MarketModel.Market("St. Jacob's"),
        MarketModel.Market("St. Lawrence"),
        MarketModel.Market("Kensington Market"),
        MarketModel.Market("St. Catherines Market"),
        MarketModel.Market("Test Market"),
    )

    fun getMarkets(): Flow<List<MarketModel.Market>> {
        return flow {
            emit(markets)
        }.flowOn(Dispatchers.IO)
    }
}