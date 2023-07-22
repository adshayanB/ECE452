package com.example.farmeraid.data

import android.util.Log
import com.example.farmeraid.data.model.ResponseModel.FAResponse
import com.example.farmeraid.data.model.ResponseModel.FAResponseWithData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// TODO: currently, we have mock demo functionality but need to modify to use firestore db after demo
// TODO: currently, we are lacking user permission checks for appropriate functions, need to add these

class InventoryRepository(
    private val userRepository: UserRepository
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
                    .get()
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
                    val produceMap : MutableMap<String, Int> = db.collection("inventory").document(it).get().await().get("produce") as MutableMap<String,Int>
                    if (!produceMap.containsKey(produceName)) {
                        db.collection("inventory").document(it).update("produce.${produceName}", produceAmount).await()
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
                            .update("produce.${produceName}", produceAmount).await()
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
                        .update("produce.${produceName}", FieldValue.delete()).await()
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
                    }).await()
                    FAResponse.Success
                } catch (e : Exception) {
                    Log.e("InventoryRepository", e.message ?: e.stackTraceToString())
                    FAResponse.Error(e.message ?: "Unknown error while while updating harvests")
                } }
            ?: FAResponse.Error("User is not part of a farm")
    }
}