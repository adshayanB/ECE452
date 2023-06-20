package com.example.farmeraid.data

import com.example.farmeraid.home.model.HomeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class InventoryRepository {
    fun getInventory(): Flow<List<HomeModel.Produce>> {
        return flow {
            val inventoryList = listOf(
                HomeModel.Produce(
                produceName = "Test Produce",
                produceAmount = 5,
            ))
            emit(inventoryList)
        }.flowOn(Dispatchers.IO)
    }
}