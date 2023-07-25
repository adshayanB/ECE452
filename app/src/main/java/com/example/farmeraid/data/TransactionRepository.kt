package com.example.farmeraid.data

import android.util.Log
import com.example.farmeraid.data.model.InventoryModel
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.ResponseModel
import com.example.farmeraid.data.model.TransactionModel
import com.example.farmeraid.transactions.model.TransactionsModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.util.Date

class TransactionRepository(
    private val userRepository: UserRepository
) {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun addTransaction(transaction: TransactionModel.Transaction) : ResponseModel.FAResponse {
        return userRepository.getFarmId()?.let {farmID ->
            val transactionMap = hashMapOf(
                "count" to transaction.produce.produceAmount.toLong(),
                "destination" to transaction.location,
                "pricePerProduce" to transaction.pricePerProduce, //To do: determine if u want to have a price for transactions, or not
                "produce" to transaction.produce.produceName,
                "timestamp" to FieldValue.serverTimestamp(),
                "type" to transaction.transactionType,
                "farmID" to farmID,
            )

            val createTransactionRef = db.collection("transaction").document()
            try {
                createTransactionRef.set(transactionMap).await()
                ResponseModel.FAResponse.Success
            } catch (e: Exception) {
                ResponseModel.FAResponse.Error(e.message ?: "Unknown error while adding transaction")
            }
        } ?: ResponseModel.FAResponse.Error("User does not exist")
    }

    suspend fun getRecentTransactions(filterList: List<TransactionsModel.Filter>, limit: Int): ResponseModel.FAResponseWithData<List<TransactionModel.Transaction>> {
        val items: QuerySnapshot = userRepository.getFarmId()?.let { id ->
            // Setup the where clauses based on the given filters
            var query = db.collection("transactions").whereEqualTo("farmID", id)
            filterList.forEach {
                if (!(it.name == TransactionsModel.FilterName.Type && it.selectedItem == TransactionModel.TransactionType.ALL.stringValue) && it.selectedItem != null) {
                    query = query.whereEqualTo(it.name.dbFieldName, it.selectedItem)
                }
            }

            try {
                query.orderBy("timestamp", Query.Direction.DESCENDING).limit(limit.toLong()).get().await()
            } catch (e: Exception) {
                Log.e("TransactionsRepository", e.message  ?: "Unknown error while fetching transactions")
                return ResponseModel.FAResponseWithData.Error(e.message ?: "Unknown error while fetching transactions")
            }
        } ?: run {
            return ResponseModel.FAResponseWithData.Error("User does not exist")
        }

        return ResponseModel.FAResponseWithData.Success(
                    items.documents.mapNotNull { transaction ->
                        transaction.data?.let {
                            TransactionModel.Transaction(
                                transactionId = transaction.id,
                                transactionType = it["type"] as String,
                                produce = InventoryModel.Produce(
                                    produceName = it["produce"] as String,
                                    produceAmount = (it["count"] as Long).toInt(),
                                ),
                                pricePerProduce = it["pricePerProduce"] as Double,
                                location = it["destination"] as String,
                            )
                        }
                    }
                )
    }

    suspend fun deleteTransaction(transactionId: String) : ResponseModel.FAResponse {
        // TODO - Undo the actual transaction before deleting the record
        try {
            db.collection("transactions").document(transactionId).delete().await()
        } catch (e: Exception) {
            return ResponseModel.FAResponse.Error(e.message ?: "Unknown error while deleting transaction")
        }

        return userRepository.getFarmId()
            ?.let {
                try {
                    db.collection("farm").document(it).update("transactions", FieldValue.arrayRemove(transactionId)).await()
                    ResponseModel.FAResponse.Success
                } catch (e: Exception) {
                    ResponseModel.FAResponse.Error(e.message ?: "Unknown message while updating farm")
                }
            } ?: ResponseModel.FAResponse.Error("User does not exist")
    }

    suspend fun getQuotaSaleAmounts(marketName: String, produce : List<String>, startOfWeek : Date, endOfWeek : Date) : ResponseModel.FAResponseWithData<MutableMap<String, Int>> {
        Log.d("QuotaSale","Start: ${startOfWeek}, End: ${endOfWeek}")
        val transactionsQuery = userRepository.getFarmId()?.let {
             db.collection("transactions")
                .whereEqualTo("farmID", it)
                .whereEqualTo("type", TransactionModel.TransactionType.SELL.stringValue)
                .whereEqualTo("destination", marketName)
                .whereGreaterThanOrEqualTo("timestamp", startOfWeek)
                .whereLessThan("timestamp", endOfWeek)
        } ?: run {
            return ResponseModel.FAResponseWithData.Error("User does not exist")
        }

        val salesMap : MutableMap<String, Int> = produce.associate {
            it to 0
        }.toMutableMap()

        produce.forEach { name ->
            try {
                salesMap[name] = transactionsQuery.whereEqualTo("produce", name).get().await().documents.mapNotNull {
                    it.data?.get("count") as Long
                }.sum().toInt()
            } catch (e : Exception) {
                Log.e("Index", e.message ?: "Unknown error")
                return ResponseModel.FAResponseWithData.Error(e.message ?: "Unknown error while")
            }
        }

        return ResponseModel.FAResponseWithData.Success(salesMap)
    }
}