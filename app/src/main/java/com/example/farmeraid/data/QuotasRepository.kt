package com.example.farmeraid.data

import android.util.Log
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.ResponseModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// TODO: currently, we have mock demo functionality but need to modify to use firestore db after demo
// TODO: currently, we are lacking user permission checks for appropriate functions, need to add these

class QuotasRepository {
    data class ProduceQuota(
        val produceName: String,
        val produceGoalAmount: Int,
        val saleAmount: Int
    )

    data class Quota(
        val id: String,
        val produceQuotaList: List<ProduceQuota>
    )

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    //Todo: Refactor to not mock sales data
    private var SALES = hashMapOf(
        "mangoes" to 4,
        "apple" to 2,
        "strawberry" to 7,
        "bananas" to 2,
        "strawberry" to 4
    )

    suspend fun getQuota(id: String): Quota? {
        val docRef = db.collection("quotas").document(id)
        val docRead = docRef.get().await()

        val quotas = docRead.data?.get("produce")
        val saleCount = docRead.data?.get("sale")

        if (quotas != null && saleCount != null) {
            val quotasMap = quotas as MutableMap<String, Int>
            val sale = saleCount as MutableMap<String, Int>
            return Quota(
                id = id,
                produceQuotaList = quotasMap.map{ (produceName, goal) ->
                    ProduceQuota(
                        produceName = produceName,
                        produceGoalAmount = goal,
                        saleAmount = sale.getOrDefault(produceName, 0),
                    )
                })
        }
        else {
            return null
        }
    }
    suspend fun addQuota(market: MarketModel.Market, produce: List<QuotasRepository.ProduceQuota>) {
        if (db.collection("quotas").document(market.id).get().await().exists()) {
            db.collection("quotas").document(market.id)
                .update("produce", produce.associate {
                    it.produceName to it.produceGoalAmount
                })
        } else {
            db.collection("quotas").document(market.id).set(
                mapOf(
                    "produce" to produce.associate {
                        it.produceName to it.produceGoalAmount
                    },
                    "sale" to emptyMap<String, Int>(),
                )
            )
        }
    }

    suspend fun deleteQuota(id : String) : ResponseModel.FAResponse {
        return try {
                    db.collection("quotas").document(id).delete().await()
                    ResponseModel.FAResponse.Success
                } catch (e: Exception) {
                    Log.e("QuotasRepository", e.message ?: e.stackTraceToString())
                    ResponseModel.FAResponse.Error(e.message ?: "Unknown error while deleting quota")
                }
    }
}