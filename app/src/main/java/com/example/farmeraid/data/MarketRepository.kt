package com.example.farmeraid.data

import android.util.Log
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.ResponseModel
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
        val marketModel = marketIds.data?.map { element ->
            db.collection("market").document(element).get().await()
        }?.toList()

        val marketModelList = mutableListOf<MarketModel.Market>()

        if (marketModel != null) {
            for (market in marketModel) {
                Log.e("ID", market.id)
                marketModelList.add(MarketModel.Market(
                    id = market.id,
                    name = market.get("name") as String,
                    quotaID = market.get("quota_id") as String
                ))
            }
        }

       return ResponseModel.FAResponseWithData.Success(marketModelList as List<MarketModel.Market>)
    }
    private suspend fun readMarketDataWithQuotas(): ResponseModel.FAResponseWithData<List<MarketModel.MarketWithQuota>> {
        val marketIds = farmRepository.getMarketIds()
        val marketModel = marketIds.data?.map { element ->
            db.collection("market").document(element).get().await()
        }?.toList()

        val marketModelList = mutableListOf<MarketModel.MarketWithQuota>()

        if (marketModel != null) {
            for (market in marketModel) {
                quotasRepository.getQuota(market.get("quota_id") as String)?.let {
                    MarketModel.MarketWithQuota(
                        id = market.id,
                        name = market.get("name") as String,
                        quota = it
                    )
                }?.let { marketModelList.add(it) }
            }
        }

        return ResponseModel.FAResponseWithData.Success(marketModelList as List<MarketModel.MarketWithQuota>)
    }

    suspend fun getMarkets(): Flow<ResponseModel.FAResponseWithData<List<MarketModel.Market>>> {
        return flow {
            emit(readMarketData())
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getMarketsWithQuota() : Flow<ResponseModel.FAResponseWithData<List<MarketModel.MarketWithQuota>>> {
        Log.e("test", readMarketData().data.toString())
        return flow {
            emit(
              readMarketDataWithQuotas()
            )
        }
    }

    suspend fun getMarketWithQuota(id : String) : MarketModel.MarketWithQuota? {
        return readMarketDataWithQuotas().data?.firstOrNull { it.id == id }
    }
}