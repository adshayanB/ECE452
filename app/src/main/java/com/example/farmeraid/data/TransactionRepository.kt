package com.example.farmeraid.data

import com.example.farmeraid.data.model.InventoryModel
import com.example.farmeraid.data.model.ResponseModel
import com.example.farmeraid.data.model.TransactionModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

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

    suspend fun getRecentTransactions(transactionType: TransactionModel.TransactionType, limit: Int): ResponseModel.FAResponseWithData<List<TransactionModel.Transaction>> {
        val items: QuerySnapshot = userRepository.getFarmId()?.let {
            try {
                if (transactionType == TransactionModel.TransactionType.ALL){
                    db.collection("transactions").whereEqualTo("farmID", it).orderBy("timestamp", Query.Direction.DESCENDING).limit(limit.toLong()).get().await()
                } else {
                    db.collection("transactions").whereEqualTo("farmID", it).whereEqualTo("type", transactionType.stringValue).orderBy("timestamp", Query.Direction.DESCENDING).limit(limit.toLong()).get().await()
                }
            } catch (e: Exception) {
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
}