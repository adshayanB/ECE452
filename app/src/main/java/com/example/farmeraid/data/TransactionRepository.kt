package com.example.farmeraid.data

import com.example.farmeraid.data.model.InventoryModel
import com.example.farmeraid.data.model.TransactionsModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class TransactionRepository(
    private val userRepository: UserRepository
) {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun addTransaction(transaction: TransactionsModel.Transaction) {
        val transactionMap = hashMapOf(
            "count" to transaction.produce.produceAmount,
            "destination" to transaction.location,
            "pricePerProduce" to transaction.pricePerProduce, //To do: determine if u want to have a price for transactions, or not
            "produce" to transaction.produce.produceName,
            "timestamp" to FieldValue.serverTimestamp(),
            "type" to transaction.transactionType,
        )

        val createTransactionRef = db.collection("transaction").document()
        createTransactionRef.set(transactionMap).await()

        //Update FarmList
        userRepository.getFarmId()
            ?.let { db.collection("farm").document(it).update("transactions", FieldValue.arrayUnion(createTransactionRef.id)).await() }
    }

    suspend fun getRecentTransactions(transactionType: TransactionsModel.TransactionType, limit: Int): Flow<List<TransactionsModel.Transaction>> {
        val farmID = userRepository.getFarmId()

        val items: QuerySnapshot = if (transactionType == TransactionsModel.TransactionType.ALL){
                db.collection("transactions").whereEqualTo("farmID", farmID).orderBy("timestamp", Query.Direction.DESCENDING).limit(limit.toLong()).get().await()
            } else {
                db.collection("transactions").whereEqualTo("farmID", farmID).whereEqualTo("type", transactionType.stringValue).orderBy("timestamp", Query.Direction.DESCENDING).limit(limit.toLong()).get().await()
            }

        return flow {
            emit(
                items.documents.mapNotNull { transaction ->
                    transaction.data?.let {
                        TransactionsModel.Transaction(
                            transactionId = transaction.id,
                            transactionType = it["type"] as String,
                            produce = InventoryModel.Produce(
                                produceName = it["produce"] as String,
                                produceAmount = it["count"] as Int,
                            ),
                            pricePerProduce = it["pricePerProduce"] as Double,
                            location = it["destination"] as String,
                        )
                    }
                }
            )
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteTransaction(transactionId: String) {
        // TODO - Undo the actual transaction before deleting the record
        db.collection("transactions").document(transactionId).delete().await()
        userRepository.getFarmId()
            ?.let { db.collection("farm").document(it).update("transactions", FieldValue.arrayRemove(transactionId)).await() }
    }
}