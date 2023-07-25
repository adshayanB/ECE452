package com.example.farmeraid.data

import android.util.Log
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.QuotaModel
import com.example.farmeraid.data.model.ResponseModel
import com.example.farmeraid.data.model.toDate
import com.example.farmeraid.data.model.toLocalDateTime
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters
import java.util.Date

// TODO: currently, we have mock demo functionality but need to modify to use firestore db after demo
// TODO: currently, we are lacking user permission checks for appropriate functions, need to add these

class QuotasRepository(
    val transactionRepository: TransactionRepository,
) {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getQuota(id: String, marketName: String): ResponseModel.FAResponseWithData<QuotaModel.Quota?> {
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

        var endOfWeek = (docRead.data?.get("endOfWeek") as Timestamp).toLocalDateTime()
        val quotas = docRead.data?.get("produce")
        var saleCount = docRead.data?.get("sale")

        if (LocalDateTime.now().isAfter(endOfWeek)) {
            endOfWeek = endOfWeek.plusWeeks(1)
            saleCount = transactionRepository.getQuotaSaleAmounts(marketName, quotas as MutableMap<String, Int>, endOfWeek.minusWeeks(1).toDate(), endOfWeek.toDate()).let {
                it.data ?: run {
                    return ResponseModel.FAResponseWithData.Error(it.error ?: "Unknown error while getting updated quota sale amounts")
                }
            }
            db.collection("quotas").document(id).update(
                mapOf(
                    "endOfWeek" to endOfWeek.toDate(),
                    "sale" to saleCount,
                )
            ).await()
        }

        Log.d("Date", "Client: ${endOfWeek.minusWeeks(1).toDate()}, Server: ${endOfWeek.toDate()}")

        return if (quotas != null && saleCount != null) {
            val quotasMap: MutableMap<String, Int> = if ((quotas as MutableMap<String, Int>).isEmpty()) quotas else (quotas).toSortedMap(String.CASE_INSENSITIVE_ORDER)
            val sale: MutableMap<String, Int> = if ((saleCount as MutableMap<String, Int>).isEmpty()) saleCount else (saleCount).toSortedMap(String.CASE_INSENSITIVE_ORDER)
            ResponseModel.FAResponseWithData.Success(
                QuotaModel.Quota(
                    id = id,
                    produceQuotaList = quotasMap.map { (produceName, goal) ->
                        QuotaModel.ProduceQuota(
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
    suspend fun addOrUpdateQuota(market: MarketModel.Market, produce: List<QuotaModel.ProduceQuota>) : ResponseModel.FAResponse {
        val doc : DocumentSnapshot = try {
            db.collection("quotas").document(market.id).get().await()
        } catch (e : Exception) {
            Log.e("QuotasRepository", e.message ?: e.stackTraceToString())
            return ResponseModel.FAResponse.Error(e.message ?: "Unknown error fetching for quota")
        }

        return try {
            if (doc.exists()) {
                db.collection("quotas").document(market.id)
                    .update(
                        "produce", produce.associate{ it.produceName to it.produceGoalAmount },
                        "sale", produce.associate{ it.produceName to it.saleAmount },
                    )
            } else {
                db.collection("quotas").document(market.id).set(
                    mapOf(
                        "produce" to produce.associate{it.produceName to it.produceGoalAmount },
                        "sale" to produce.associate{ it.produceName to it.saleAmount },
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