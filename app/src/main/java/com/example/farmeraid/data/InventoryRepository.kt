package com.example.farmeraid.data

import android.util.Log
import com.cesarferreira.pluralize.pluralize
import com.example.farmeraid.data.model.ResponseModel.FAResponse
import com.example.farmeraid.data.model.ResponseModel.FAResponseWithData
import com.example.farmeraid.data.source.NetworkMonitor
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class InventoryRepository(
    private val userRepository: UserRepository,
    private val networkMonitor: NetworkMonitor,
) {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    suspend fun getInventory(): Flow<FAResponseWithData<MutableMap<String,Int>>> {
        return flow {
            emit(readInventoryData())
        }.flowOn(Dispatchers.IO)
    }
    private suspend fun readInventoryData(): FAResponseWithData<MutableMap<String, Int>> {
        return userRepository.getFarmId()?.let { id ->
            try {
                db.collection("inventory").document(id)
                    .get(networkMonitor.getSource())
                    .await()
                    .data?.get("produce")?.let {
                        FAResponseWithData.Success((it as MutableMap<String, Int>).toSortedMap(String.CASE_INSENSITIVE_ORDER))
                    } ?: FAResponseWithData.Error("Error fetching produce")
            } catch (e : Exception) {
                Log.e("InventoryRepository", e.message ?: e.stackTraceToString())
                FAResponseWithData.Error(e.message ?: "Unknown error while getting inventory")
            }
        } ?: FAResponseWithData.Error("User is not part of a farm")
    }

    suspend fun addNewProduce(produceName: String, produceAmount: Int) : FAResponse {
        return userRepository.getFarmId()
            ?.let {
                try {
                    val produceMap : MutableMap<String, Int> = db.collection("inventory").document(it).get(networkMonitor.getSource()).await().get("produce") as MutableMap<String,Int>
                    if (!produceMap.containsKey(produceName)) {
                        db.collection("inventory").document(it).update("produce.${produceName}", produceAmount)
                        FAResponse.Success
                    } else {
                        FAResponse.Error("This produce already exists")
                    }

                } catch (e : Exception) {
                    Log.e("InventoryRepository", e.message ?: e.stackTraceToString())
                    FAResponse.Error(e.message ?: "Unknown error while adding new produce")
                }
            } ?: FAResponse.Error("User is not part of a farm")
    }

    suspend fun editProduce(produceName: String, produceAmount: Int) : FAResponse {
        return userRepository.getFarmId()
                ?.let {
                    try {
                        db.collection("inventory").document(it)
                            .update("produce.${produceName}", produceAmount)
                        FAResponse.Success
                    } catch (e: Exception) {
                        Log.e("InventoryRepository", e.message ?: e.stackTraceToString())
                        FAResponse.Error(e.message ?: "Unknown error while editing produce")
                    }
                } ?: FAResponse.Error("User is not part of a farm")
    }

    suspend fun deleteProduce(produceName: String) : FAResponse {
        return userRepository.getFarmId()
            ?.let {
                try {
                    db.collection("inventory").document(it)
                        .update("produce.${produceName}", FieldValue.delete())
                    FAResponse.Success
                } catch (e: Exception) {
                    Log.e("InventoryRepository", e.message ?: e.stackTraceToString())
                    FAResponse.Error(e.message ?: "Unknown error while editing produce")
                }
            } ?: FAResponse.Error("User is not part of a farm")
    }

    suspend fun harvest(harvestChanges: Map<String, Int>) : FAResponse {
        return userRepository.getFarmId()
            ?.let {
                try {
                    db.collection("inventory").document(it).update(harvestChanges.entries.associate{
                            (produceName, produceAmount) ->
                        "produce.${produceName}" to FieldValue.increment(produceAmount.toLong())
                    })
                    FAResponse.Success
                } catch (e : Exception) {
                    Log.e("InventoryRepository", e.message ?: e.stackTraceToString())
                    FAResponse.Error(e.message ?: "Unknown error while while updating harvests")
                } }
            ?: FAResponse.Error("User is not part of a farm")
    }

    suspend fun sell(sellChanges: Map<String, Int>) : FAResponse {
        return userRepository.getFarmId()
            ?.let {
                try {
                    val inventory = db.collection("inventory").document(it).get(networkMonitor.getSource()).await().let { res ->
                        res.data ?: run {
                            return FAResponse.Error("Error while getting inventory")
                        }
                    }
                    val produceMap = inventory["produce"] as MutableMap<String, Int>
                    val errorEntry = sellChanges.entries.firstOrNull { (name, count) ->
                        count > (produceMap[name] as Long)
                    }

                    if (errorEntry != null) return FAResponse.Error("You do not have enough ${errorEntry.key.pluralize()} to perform this operation")

                    db.collection("inventory").document(it).update(sellChanges.entries.associate{
                            (produceName, produceAmount) ->
                        "produce.${produceName}" to FieldValue.increment(-1 * produceAmount.toLong())
                    })
                    FAResponse.Success
                } catch (e : Exception) {
                    Log.e("InventoryRepository", e.message ?: e.stackTraceToString())
                    FAResponse.Error(e.message ?: "Unknown error while while updating sell")
                } }
            ?: FAResponse.Error("User is not part of a farm")
    }
}