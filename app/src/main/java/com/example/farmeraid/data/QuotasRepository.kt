package com.example.farmeraid.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.QuotaModel
import com.example.farmeraid.data.model.ResponseModel
import com.example.farmeraid.data.model.toDate
import com.example.farmeraid.data.model.toLocalDateTime
import com.example.farmeraid.data.source.NetworkMonitor
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

class QuotasRepository(
    private val transactionRepository: TransactionRepository,
    private val networkMonitor: NetworkMonitor,
) {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getQuota(id: String, marketName: String): ResponseModel.FAResponseWithData<QuotaModel.Quota?> {
        val docRead : DocumentSnapshot
        try {
            docRead = db.collection("quotas").document(id).get(networkMonitor.getSource()).await()
        } catch (e : Exception) {
            Log.e("QuotasRepository",e.message ?: e.stackTraceToString())
            return ResponseModel.FAResponseWithData.Error(e.message ?: "Unknown error while fetching quota")
        }

        if (!docRead.exists()) {
            return ResponseModel.FAResponseWithData.Error("Quota does not exist")
        }

        var endOfWeek = (docRead.data?.get("endOfWeek") as Timestamp).toLocalDateTime()
        val quotas : MutableMap<String, Int> = docRead.data?.get("produce") as MutableMap<String, Int>? ?: run {
            return ResponseModel.FAResponseWithData.Error("Quota object does not have produce information")
        }
        var saleCount = docRead.data?.get("sale") as MutableMap<String, Int>? ?: run {
            return ResponseModel.FAResponseWithData.Error("Quota object does not have sale information")
        }

        if (LocalDateTime.now().isAfter(endOfWeek)) {
            endOfWeek = endOfWeek.plusWeeks(1)
            saleCount = transactionRepository.getQuotaSaleAmounts(marketName, quotas.map { it.key }, endOfWeek.minusWeeks(1).toDate(), endOfWeek.toDate()).let {
                it.data ?: run {
                    return ResponseModel.FAResponseWithData.Error(it.error ?: "Unknown error while getting updated quota sale amounts")
                }
            }
            try {
                db.collection("quotas").document(id).update(
                    mapOf(
                        "endOfWeek" to endOfWeek.toDate(),
                        "sale" to saleCount,
                    )
                )
            } catch (e : Exception) {
                return ResponseModel.FAResponseWithData.Error(e.message ?: "Unknown error while updating quota sale amounts")
            }
        }

        val quotasMap: MutableMap<String, Int> = if (quotas.isEmpty()) quotas else quotas.toSortedMap(String.CASE_INSENSITIVE_ORDER)
        val sale: MutableMap<String, Int> = if (saleCount.isEmpty()) saleCount else saleCount.toSortedMap(String.CASE_INSENSITIVE_ORDER)
        return ResponseModel.FAResponseWithData.Success(
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
    suspend fun addOrUpdateQuota(market: MarketModel.Market, produce: List<QuotaModel.ProduceQuota>) : ResponseModel.FAResponse {
        val doc : DocumentSnapshot = try {
            db.collection("quotas").document(market.id).get(networkMonitor.getSource()).await()
        } catch (e : Exception) {
            Log.e("QuotasRepository", e.message ?: e.stackTraceToString())
            return ResponseModel.FAResponse.Error(e.message ?: "Unknown error fetching for quota")
        }

        return try {
            if (doc.exists()) {
                val endOfWeek = (doc.data?.get("endOfWeek") as Timestamp).toLocalDateTime()
                val startOfWeek = endOfWeek.minusWeeks(1)
                val saleMap : MutableMap<String, Int> = transactionRepository.getQuotaSaleAmounts(market.name, produce.map { it.produceName }, startOfWeek.toDate(), endOfWeek.toDate()).let {
                    it.data ?: run {
                        return ResponseModel.FAResponse.Error(it.error ?: "Unknown error while getting updated quota sale amounts")
                    }
                }
                db.collection("quotas").document(market.id)
                    .update(
                        "produce", produce.associate{ it.produceName to it.produceGoalAmount },
                        "sale", saleMap,
                    )
            } else {
                val endOfWeek = LocalDateTime.now().with(
                    LocalTime.MIDNIGHT
                )
                    .with(
                        TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)
                    )
                val startOfWeek = endOfWeek.minusWeeks(1)
                val saleMap : MutableMap<String, Int> = transactionRepository.getQuotaSaleAmounts(market.name, produce.map { it.produceName }, startOfWeek.toDate(), endOfWeek.toDate()).let {
                    it.data ?: run {
                        return ResponseModel.FAResponse.Error(it.error ?: "Unknown error while getting updated quota sale amounts")
                    }
                }

                db.collection("quotas").document(market.id).set(
                    mapOf(
                        "endOfWeek" to endOfWeek.toDate(),
                        "produce" to produce.associate{it.produceName to it.produceGoalAmount },
                        "sale" to saleMap,
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
                    db.collection("quotas").document(id).delete()
                    ResponseModel.FAResponse.Success
                } catch (e: Exception) {
                    Log.e("QuotasRepository", e.message ?: e.stackTraceToString())
                    ResponseModel.FAResponse.Error(e.message ?: "Unknown error while deleting quota")
                }
    }
}