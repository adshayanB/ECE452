package com.example.farmeraid.data

import android.util.Log
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.ResponseModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
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


    suspend fun getQuota(id: String): ResponseModel.FAResponseWithData<Quota?> {
        val docRead : DocumentSnapshot
        try {
            docRead = db.collection("quotas").document(id).get().await()
        } catch (e : Exception) {
            Log.e("QuotasRepository",e.message ?: e.stackTraceToString())
            return ResponseModel.FAResponseWithData.Error(e.message ?: "Unknown error while fetching quota")
        }

        if (!docRead.exists()) {
            return ResponseModel.FAResponseWithData.Error("Quota does not exist")
        }

        val quotas = docRead.data?.get("produce")
        val saleCount = docRead.data?.get("sale")

        return if (quotas != null && saleCount != null) {
            val quotasMap = quotas as MutableMap<String, Int>
            val sale = saleCount as MutableMap<String, Int>
            ResponseModel.FAResponseWithData.Success(
                Quota(
                    id = id,
                    produceQuotaList = quotasMap.map{ (produceName, goal) ->
                        ProduceQuota(
                            produceName = produceName,
                            produceGoalAmount = goal,
                            saleAmount = sale.getOrDefault(produceName, 0),
                        )
                    }
                )
            )
        }
        else {
            ResponseModel.FAResponseWithData.Error("Quota object does not have produce or sale information")
        }
    }
    suspend fun addQuota(market: MarketModel.Market, produce: List<ProduceQuota>) : ResponseModel.FAResponse {
        val doc : DocumentSnapshot = try {
            db.collection("quotas").document(market.id).get().await()
        } catch (e : Exception) {
            Log.e("QuotasRepository", e.message ?: e.stackTraceToString())
            return ResponseModel.FAResponse.Error(e.message ?: "Unknown error fetching for quota")
        }

        return try {
            if (doc.exists()) {
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
            ResponseModel.FAResponse.Success
        } catch (e : Exception) {
            ResponseModel.FAResponse.Error(e.message ?: e.stackTraceToString())
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