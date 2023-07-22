package com.example.farmeraid.data

import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.ResponseModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class MarketRepository(
    private val quotasRepository: QuotasRepository,
    private val farmRepository: FarmRepository
) {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private suspend fun readMarketData(): ResponseModel.FAResponseWithData<List<MarketModel.Market>> {
        val marketIds = farmRepository.getMarketIds()
        if (marketIds.error != null) {
            return ResponseModel.FAResponseWithData.Error(marketIds.error)
        }

        val marketModel: MutableList<DocumentSnapshot> = mutableListOf()
        marketIds.data?.forEach {
            try {
                marketModel.add(db.collection("market").document(it).get().await())
            } catch (e: Exception) {
                return ResponseModel.FAResponseWithData.Error(
                    e.message ?: "Unknown error while fetching market"
                )
            }
        }

        val marketModelList = mutableListOf<MarketModel.Market>()

        for (market in marketModel) {
            MarketModel.Market(
                id = market.id,
                name = market.get("name") as String,
            )
                .let { marketModelList.add(it) }
        }

        marketModelList.sortBy { it.name.lowercase() }
        return ResponseModel.FAResponseWithData.Success(marketModelList)
    }

    private suspend fun readMarketDataWithQuotas(): ResponseModel.FAResponseWithData<List<MarketModel.MarketWithQuota>> {
        val marketIds = farmRepository.getMarketIds()
        if (marketIds.error != null) {
            return ResponseModel.FAResponseWithData.Error(marketIds.error)
        }

        val marketModel : MutableList<DocumentSnapshot> = mutableListOf()
        marketIds.data?.forEach {
            try {
                marketModel.add(db.collection("market").document(it).get().await())
            } catch (e : Exception) {
                return ResponseModel.FAResponseWithData.Error(e.message ?: "Unknown error while fetching market")
            }
        }

        val marketModelList = mutableListOf<MarketModel.MarketWithQuota>()

        for (market in marketModel) {
            quotasRepository.getQuota(market.id).let { quotaResponse ->
                quotaResponse.data?.let {
                    marketModelList.add(
                        MarketModel.MarketWithQuota(
                            id = market.id,
                            name = market.get("name") as String,
                            quota = it
                        )
                    )
                } ?: run {
                    if (quotaResponse.error != "Quota does not exist") {
                        return ResponseModel.FAResponseWithData.Error(quotaResponse.error ?: "Unknown error while getting quotas")
                    }
                }

            }
        }
        marketModelList.sortBy { it.name.lowercase() }
        return ResponseModel.FAResponseWithData.Success(marketModelList)
    }

    suspend fun getMarkets(): Flow<ResponseModel.FAResponseWithData<List<MarketModel.Market>>> {
        return flow {
            emit(readMarketData())
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getMarketsWithQuota() : Flow<ResponseModel.FAResponseWithData<List<MarketModel.MarketWithQuota>>> {
        return flow {
            emit(
                readMarketDataWithQuotas()
            )
        }
    }

    suspend fun getMarketWithQuota(id : String) : ResponseModel.FAResponseWithData<MarketModel.MarketWithQuota> {

        val marketModel : DocumentSnapshot = try {
                db.collection("market").document(id).get().await()
            } catch (e : Exception) {
                return ResponseModel.FAResponseWithData.Error(e.message ?: "Unknown error while fetching market")
            }

        if (!marketModel.exists()) {
            return ResponseModel.FAResponseWithData.Error("Market does not exist")
        }

        val marketWithQuota = quotasRepository.getQuota(marketModel.id).let { quotaResponse ->
                quotaResponse.data?.let {
                    MarketModel.MarketWithQuota(
                        id = marketModel.id,
                        name = marketModel.get("name") as String,
                        quota = it
                    )
                } ?: run {
                    return ResponseModel.FAResponseWithData.Error(quotaResponse.error ?: "Unknown error while fetching market's quota")
                }
            }

        return ResponseModel.FAResponseWithData.Success(marketWithQuota)
    }
}