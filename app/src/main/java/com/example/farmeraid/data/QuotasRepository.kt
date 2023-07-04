package com.example.farmeraid.data

import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.home.model.HomeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

// TODO: currently, we have mock demo functionality but need to modify to use firestore db after demo
// TODO: currently, we are lacking user permission checks for appropriate functions, need to add these

class QuotasRepository {
    data class ProduceQuota(
        val produceName : String,
        val produceGoalAmount : Int,
    )

    data class Quota(
        val id: String,
        val produceQuotaList : List<ProduceQuota>,
    )

    private var currentId : String = "5"

    private val quotasList: MutableList<Quota> = mutableListOf(Quota(
        id = "1",
        produceQuotaList = listOf(
            ProduceQuota(
                produceName = "Apple",
                produceGoalAmount = 25,
            ),
            ProduceQuota(
                produceName = "Banana",
                produceGoalAmount = 10,
            ),
            ProduceQuota(
                produceName = "Mango",
                produceGoalAmount = 1,
            ),
            ProduceQuota(
                produceName = "Strawberry",
                produceGoalAmount = 2,
            ),
        )
    ), Quota(
        id = "2",
        produceQuotaList = listOf(
            ProduceQuota(
                produceName = "Strawberry",
                produceGoalAmount = 15,
            ),
            ProduceQuota(
                produceName = "Apple",
                produceGoalAmount = 24,
            ),
        )
    ), Quota(
        id = "3",
        produceQuotaList = listOf(
            ProduceQuota(
                produceName = "Apple",
                produceGoalAmount = 4,
            ),
            ProduceQuota(
                produceName = "Banana",
                produceGoalAmount = 10,
            ),
            ProduceQuota(
                produceName = "Mango",
                produceGoalAmount = 5,
            ),
        )
    ), Quota(
        id = "4",
        produceQuotaList = listOf(
            ProduceQuota(
                produceName = "Banana",
                produceGoalAmount = 5,
            ),
            ProduceQuota(
                produceName = "Mango",
                produceGoalAmount = 12,
            ),
        )
    ),
    )

    fun getCategorizedQuotas(): Flow<MutableList<Quota>> {
        return flow {
            emit(quotasList)
        }.flowOn(Dispatchers.IO)
    }

    fun getQuota(id : String): Quota? {
        return quotasList.firstOrNull { quota -> quota.id == id }
    }

    fun addQuota(market : MarketModel.Market, produce : List<ProduceQuota>) : String? {
        val quotaIndex : Int = quotasList.indexOfFirst { quota -> quota.id == market.quotaId }

        return if (quotaIndex >= 0) {
            quotasList[quotaIndex] = quotasList[quotaIndex].copy(produceQuotaList = produce)
            null
        } else {
            quotasList.add(Quota(
                id = currentId,
                produceQuotaList = produce,
            ))
            val tempId = currentId
            currentId = (currentId.toInt() + 1).toString()
            tempId
        }
    }
}