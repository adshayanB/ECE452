package com.example.farmeraid.data

import com.example.farmeraid.data.model.MarketModel
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
    fun addQuota(market: MarketModel.Market, produce: List<QuotasRepository.ProduceQuota>) {
        db.collection("quotas").document(market.quotaID)
            .update("produce", produce.associate {
                it.produceName to it.produceGoalAmount
            })
    }
}