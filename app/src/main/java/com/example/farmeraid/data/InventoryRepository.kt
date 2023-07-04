package com.example.farmeraid.data

import android.util.Log
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
    private val inventory: MutableMap<String, Int> = mutableMapOf<String, Int>().apply {
        put("Apple", 15)
        put("Banana", 20)
        put("Orange", 50)
        put("Strawberry", 30)
        put("Mango", 10)
    }
    suspend fun getInventory(): Flow<MutableMap<String, Int>> {
        return flow {
            emit(readInventoryData())
        }.flowOn(Dispatchers.IO)
    }
    private suspend fun readInventoryData(): MutableMap<String, Int> {
        val docRef = userRepository.getFarmId()?.let { db.collection("inventory").document(it) }

        val map  = docRef?.get()?.await()?.data?.get("produce")
        return if (map != null) {
            map as MutableMap<String, Int>
        } else{
            mutableMapOf()
        }
    }

    fun addNewProduce(produceName: String, produceAmount: Int) {
        if (inventory.containsKey(produceName)) { }
        else {
            inventory[produceName] = produceAmount
        }
    }

    fun editProduce(produceName: String, produceAmount: Int) {
        try {
            userRepository.getFarmId()
                ?.let { db.collection("inventory").document(it).update("produce.${produceName}", produceAmount) }
        } catch (e : Exception) {
            Log.e("InventoryRepository", e.message ?: e.stackTraceToString())
        }
    }

    fun harvest(harvestChanges: Map<String, Int> ) {
        try {
            userRepository.getFarmId()
                ?.let { db.collection("inventory").document(it).update(harvestChanges.entries.associate{
                        (produceName, produceAmount) ->
                    "produce.${produceName}" to FieldValue.increment(produceAmount.toLong())
                }) }
        } catch (e : Exception) {
            Log.e("InventoryRepository", e.message ?: e.stackTraceToString())
        }
    }
}