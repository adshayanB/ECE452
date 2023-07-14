package com.example.farmeraid.data

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date

// TODO: currently, we have mock demo functionality but need to modify to use firestore db after demo
// TODO: currently, we are lacking user permission checks for appropriate functions, need to add these

class TransactionRepository(
    private val farmRepository: FarmRepository,
    private val userRepository: UserRepository
) {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    enum class TransactionType(val stringValue: String) {
        HARVEST("HARVEST"),
        SELL("SELL"),
        DONATE("DONATE"),
        ALL("All");
    }
    data class Transaction(
        val transactionId: String,
        val transactionType: String,
        val produceChanges: MutableMap<String, Long>,
        val locationName: String, // either market name for "SELL" or community fridge name for "DONATE"
        val transactionMessage: String
    )

    suspend fun addNewTransaction(transactionType: TransactionType, produceChanges: MutableMap<String, Long>, locationName: String) {
        val transactionMessage: StringBuilder = StringBuilder()

        when (transactionType) {
            TransactionType.HARVEST -> transactionMessage.append("Harvested ")
            TransactionType.SELL -> transactionMessage.append("Sold ")
            TransactionType.DONATE -> transactionMessage.append("Donated ")
            TransactionType.ALL -> {}
        }
        var pName: String? = null
        var count: Long? = null

        for ((produceName, produceAmount) in produceChanges.entries) {
            transactionMessage.append("$produceAmount $produceName, ")
            pName = produceName
            count = produceAmount
        }

        transactionMessage.setLength(transactionMessage.length - 2)

        if (locationName.isNotEmpty()) transactionMessage.append(" to $locationName")

        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val date = Date()
        val current = formatter.format(date)

        val transaction = hashMapOf(
            "count" to count,
            "date" to current,
            "destination" to locationName,
            "price" to 10, //To do: determine if u want to have a price for transactions, or not
            "produce" to pName,
            "timestamp" to FieldValue.serverTimestamp(),
            "type" to transactionType.stringValue
        )

        val createTransactionRef = db.collection("transaction").document()
        val id = createTransactionRef.id
        createTransactionRef.set(transaction).await()

        //Update FarmList
        userRepository.getFarmId()
            ?.let { db.collection("farm").document(it).update("transactions", FieldValue.arrayUnion(id)).await() }

    }

     suspend fun getRecentTransactions(transactionType: TransactionType, limit: Int): Flow<List<Transaction>> {
        val farmID = userRepository.getFarmId()
         val transList: MutableList<Transaction> = mutableListOf()
         var items: QuerySnapshot? = null
         items = if (transactionType == TransactionType.ALL){
             db.collection("transactions").whereEqualTo("farmID", farmID).orderBy("timestamp", Query.Direction.DESCENDING).limit(limit.toLong()).get().await()
         } else{
             db.collection("transactions").whereEqualTo("farmID", farmID).whereEqualTo("type", transactionType.stringValue).orderBy("timestamp", Query.Direction.DESCENDING).limit(limit.toLong()).get().await()
         }

         for (i in 0 until items.size()){
             var res = items.documents[i].data
             var id = items.documents[i].id
             if (res != null) {
                 transList.add(Transaction(
                     transactionId = id, transactionType = res["type"] as String, locationName = res["destination"] as String, transactionMessage = res["message"] as String, produceChanges = mapOf<String, Long>(res["produce"] as String to res["count"] as Long) as MutableMap<String, Long>
                 ))
                 Log.d("ID", id)
             }

         }

         return flow {
             emit(transList)
         }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteTransaction(transactionId: String) {
        db.collection("transactions").document(transactionId).delete().await()
        userRepository.getFarmId()
            ?.let { db.collection("farm").document(it).update("transactions", FieldValue.arrayRemove(transactionId)).await() }
    }
}