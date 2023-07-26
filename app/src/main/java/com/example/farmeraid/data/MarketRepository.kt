package com.example.farmeraid.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.ResponseModel
import com.example.farmeraid.data.source.NetworkMonitor
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class MarketRepository(
    private val farmRepository: FarmRepository,
    private val userRepository: UserRepository,
    private val quotasRepository: QuotasRepository,
    private val networkMonitor: NetworkMonitor,
) {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun addOrUpdateMarket(marketName: String, producePrices: Map<String, Double>) : ResponseModel.FAResponse {

        val querySnapshot = db.collection("market").whereEqualTo("name", marketName).get(networkMonitor.getSource()).await()

        if (querySnapshot.isEmpty) {
            return try {
                val docRef = db.collection("market").add(
                    mapOf (
                        "name" to marketName,
                        "prices" to producePrices,
                        "sale_count" to 0.0
                    )
                ).await()

                userRepository.getUserId()?.let{
                    db.collection("farm").document(userRepository.getFarmId()!!).update(
                        "markets", FieldValue.arrayUnion(docRef.id)
                    )
                }

                ResponseModel.FAResponse.Success
            } catch(e: Exception) {
                return ResponseModel.FAResponse.Error(e.message?:"Error creating a market. Please try again.")
            }
        } else {
            return try {
                db.collection("market").document(querySnapshot.documents[0].id).update(
                    "prices", producePrices
                )

                ResponseModel.FAResponse.Success
            } catch(e: Exception) {
                return ResponseModel.FAResponse.Error(e.message?:"Error updating the market. Please try again.")
            }
        }
    }
    private suspend fun readMarketData(): ResponseModel.FAResponseWithData<List<MarketModel.Market>> {
        val marketIds = farmRepository.getMarketIds()
        if (marketIds.error != null) {
            return ResponseModel.FAResponseWithData.Error(marketIds.error)
        }

        val marketModel: MutableList<DocumentSnapshot> = mutableListOf()
        marketIds.data?.forEach {
            try {
                marketModel.add(db.collection("market").document(it).get(networkMonitor.getSource()).await())
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
                prices = (market.get("prices") as MutableMap<String, Double>).toSortedMap(String.CASE_INSENSITIVE_ORDER),
                saleCount = market.get("sale_count") as Double,
            )
                .let { marketModelList.add(it) }
        }

        marketModelList.sortBy { it.name.lowercase() }
        return ResponseModel.FAResponseWithData.Success(marketModelList)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun readMarketDataWithQuotas(): ResponseModel.FAResponseWithData<List<MarketModel.MarketWithQuota>> {
        val marketIds = farmRepository.getMarketIds()
        if (marketIds.error != null) {
            return ResponseModel.FAResponseWithData.Error(marketIds.error)
        }

        val marketModel : MutableList<DocumentSnapshot> = mutableListOf()
        marketIds.data?.forEach {
            try {
                marketModel.add(db.collection("market").document(it).get(networkMonitor.getSource()).await())
            } catch (e : Exception) {
                return ResponseModel.FAResponseWithData.Error(e.message ?: "Unknown error while fetching market")
            }
        }

        val marketModelList = mutableListOf<MarketModel.MarketWithQuota>()

        for (market in marketModel) {
            quotasRepository.getQuota(market.id, market["name"] as String).let { quotaResponse ->
                Log.d("market Count", "Count: ${market.get("sale_count")}")
                quotaResponse.data?.let {
                    marketModelList.add(
                        MarketModel.MarketWithQuota(
                            id = market.id,
                            name = market.get("name") as String,
                            quota = it,
                            prices = (market.get("prices") as MutableMap<String, Double>).toSortedMap(String.CASE_INSENSITIVE_ORDER),
                            saleCount = market.get("sale_count") as Double,
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getMarketsWithQuota() : Flow<ResponseModel.FAResponseWithData<List<MarketModel.MarketWithQuota>>> {
        return flow {
            emit(
                readMarketDataWithQuotas()
            )
        }
    }

    suspend fun updateSaleCount(id : String, earningsAmount : Double) : ResponseModel.FAResponse {
        val marketModel : DocumentSnapshot = try {
            db.collection("market").document(id).get(networkMonitor.getSource()).await()
        } catch (e : Exception) {
            return ResponseModel.FAResponse.Error(e.message ?: "Unknown error while fetching market")
        }

        if (!marketModel.exists()) {
            return ResponseModel.FAResponse.Error("Market does not exist")
        }

        Log.d("Sell Update", "${earningsAmount}")

        db.collection("market").document(id).update(mapOf(
            "sale_count" to  FieldValue.increment(earningsAmount)
        ))

        return ResponseModel.FAResponse.Success
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getMarketWithQuota(id : String) : ResponseModel.FAResponseWithData<MarketModel.MarketWithQuota> {

        val marketModel : DocumentSnapshot = try {
                db.collection("market").document(id).get(networkMonitor.getSource()).await()
            } catch (e : Exception) {
                return ResponseModel.FAResponseWithData.Error(e.message ?: "Unknown error while fetching market")
            }

        if (!marketModel.exists()) {
            return ResponseModel.FAResponseWithData.Error("Market does not exist")
        }

        val marketWithQuota = quotasRepository.getQuota(marketModel.id, marketModel["name"] as String).let { quotaResponse ->
                quotaResponse.data?.let {
                    MarketModel.MarketWithQuota(
                        id = marketModel.id,
                        name = marketModel.get("name") as String,
                        quota = it,
                        prices = (marketModel.get("prices") as MutableMap<String, Double>).toSortedMap(String.CASE_INSENSITIVE_ORDER),
                        saleCount = marketModel.get("sale_count") as Double,
                    )
                } ?: run {
                    return ResponseModel.FAResponseWithData.Error(quotaResponse.error ?: "Unknown error while fetching market's quota")
                }
            }

        return ResponseModel.FAResponseWithData.Success(marketWithQuota)
    }

    suspend fun getMarket(id : String) : ResponseModel.FAResponseWithData<MarketModel.Market> {

        val marketModel: DocumentSnapshot = try {
            db.collection("market").document(id).get(networkMonitor.getSource()).await()
        } catch (e : Exception) {
            return ResponseModel.FAResponseWithData.Error(e.message ?: "Unknown error while fetching market")
        }

        if (!marketModel.exists()) {
            return ResponseModel.FAResponseWithData.Error("Market does not exist")
        }

        val market: MarketModel.Market = marketModel.let { marketModel ->
            MarketModel.Market(
                id = marketModel.id,
                name = marketModel.get("name") as String,
                prices = (marketModel.get("prices") as MutableMap<String, Double>).toSortedMap(String.CASE_INSENSITIVE_ORDER),
                saleCount = marketModel.get("sale_count") as Double,
            )
        }

        return ResponseModel.FAResponseWithData.Success(market)
    }

    suspend fun getMarketFromName(name : String) : ResponseModel.FAResponseWithData<MarketModel.Market> {
        val marketModel: QuerySnapshot = try {
            db.collection("market").whereEqualTo("name", name).get(networkMonitor.getSource()).await()
        } catch (e : Exception) {
            return ResponseModel.FAResponseWithData.Error(e.message ?: "Unknown error while fetching market")
        }

        if (!marketModel.documents[0].exists()) {
            return ResponseModel.FAResponseWithData.Error("Market does not exist")
        }

        val market: MarketModel.Market = marketModel.let { marketModel ->
            MarketModel.Market(
                id = marketModel.documents[0].id,
                name = marketModel.documents[0].get("name") as String,
                prices = (marketModel.documents[0].get("prices") as MutableMap<String, Double>).toSortedMap(String.CASE_INSENSITIVE_ORDER),
                saleCount = marketModel.documents[0].get("sale_count") as Double,
            )
        }

        return ResponseModel.FAResponseWithData.Success(market)
    }
}