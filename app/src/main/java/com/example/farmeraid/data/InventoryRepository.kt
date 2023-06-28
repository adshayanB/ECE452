package com.example.farmeraid.data

import com.example.farmeraid.data.model.InventoryModel
import com.example.farmeraid.home.model.HomeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

// TODO: currently, we have mock demo functionality but need to modify to use firestore db after demo
// TODO: currently, we are lacking user permission checks for appropriate functions, need to add these

class InventoryRepository {

    private val inventory: MutableMap<String, Int> = mutableMapOf<String, Int>().apply {
        put("Apple", 15)
        put("Banana", 20)
        put("Orange", 50)
        put("Strawberry", 30)
        put("Mango", 10)
    }

    fun getInventory(): Flow<MutableMap<String, Int>> {
        return flow {
            emit(inventory)
        }.flowOn(Dispatchers.IO)
    }

    fun addNewProduce(produceName: String, produceAmount: Int) {
        if (inventory.containsKey(produceName)) { }
        else {
            inventory[produceName] = produceAmount
        }
    }

    fun editProduce(produceName: String, produceAmount: Int) {
        if (!inventory.containsKey(produceName)) { }
        else inventory[produceName] = produceAmount
    }

    fun harvest(harvestChanges: MutableMap<String, Int> ) {
        for ((produceName, produceAmount) in harvestChanges.entries) {
            inventory[produceName] = inventory[produceName]!! + produceAmount
        }
    }
}