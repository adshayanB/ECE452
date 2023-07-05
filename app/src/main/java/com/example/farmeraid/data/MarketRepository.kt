package com.example.farmeraid.data

import android.util.Log
import com.example.farmeraid.data.model.MarketModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MarketRepository(
    private val quotasRepository: QuotasRepository
) {
    private val markets: MutableList<MarketModel.Market> = mutableListOf(
        MarketModel.Market(id = "NanrSSGcmdsG2nYwbZRu", name = "St. Jacob's"),
        MarketModel.Market(id = "NanrSSGcmdsG2nYwbZRu", name = "St. Lawrence"),
        MarketModel.Market(id = "NanrSSGcmdsG2nYwbZRu",name = "Kensington Market"),
        MarketModel.Market(id = "NanrSSGcmdsG2nYwbZRu",name = "St. Catherines Market"),
        MarketModel.Market(id = "5", name = "Test Market"),
    )

    fun getMarkets(): Flow<List<MarketModel.Market>> {
        return flow {
            emit(markets)
        }.flowOn(Dispatchers.IO)
    }

    fun getMarketsWithQuota() : Flow<List<MarketModel.MarketWithQuota>> {
        return flow {
            emit(
                markets.mapNotNull { market ->
                    quotasRepository.getQuota(market.id)
                        ?.let { quota ->
                            MarketModel.MarketWithQuota(
                                id = market.id,
                                name = market.name,
                                quota = quota,
                            )
                        }
                }
            )
        }
    }

    fun getMarket(id: String) : MarketModel.Market? {
        return markets.firstOrNull { it.id == id }
    }

    suspend fun getMarketWithQuota(id : String) : MarketModel.MarketWithQuota? {
        return markets.firstOrNull { it.id == id }
            ?.let { market ->
                quotasRepository.getQuota(market.id)
                    ?.let { quota ->
                        MarketModel.MarketWithQuota(
                            id = market.id,
                            name = market.name,
                            quota = quota,
                        )
                    }
            }
    }

//    fun updateMarketQuota(marketId : String) {
//        val marketIndex : Int = markets.indexOfFirst { it.id == marketId }
//        if (marketIndex >= 0) {
//            markets[marketIndex] = markets[marketIndex].copy(id = marketId)
//        } else {
//            Log.e("MarketRepository", "Market with id $marketId does not exist")
//        }
//    }
}