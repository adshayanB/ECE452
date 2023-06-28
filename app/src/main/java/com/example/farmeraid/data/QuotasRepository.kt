package com.example.farmeraid.data

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
        val produceSoldAmount : Int,
        val produceGoalAmount : Int,
    )

    data class Quota(
        val marketName : String,
        val produceQuotaList : List<ProduceQuota>,
    )
    
    private val quotasList: MutableList<Quota> = mutableListOf(Quota(
        marketName = "St. Jacob's",
        produceQuotaList = listOf(
            ProduceQuota(
                produceName = "Apple",
                produceGoalAmount = 25,
                produceSoldAmount = 10,
            ),
            ProduceQuota(
                produceName = "Banana",
                produceGoalAmount = 10,
                produceSoldAmount = 9,
            ),
        )
    ), Quota(
        marketName = "St. Lawrence",
        produceQuotaList = listOf(
            ProduceQuota(
                produceName = "Strawberry",
                produceGoalAmount = 15,
                produceSoldAmount = 10,
            ),
            ProduceQuota(
                produceName = "Apple",
                produceGoalAmount = 24,
                produceSoldAmount = 7,
            ),
        )
    ), Quota(
        marketName = "Kenzington Market",
        produceQuotaList = listOf(
            ProduceQuota(
                produceName = "Apple",
                produceGoalAmount = 4,
                produceSoldAmount = 3,
            ),
            ProduceQuota(
                produceName = "Banana",
                produceGoalAmount = 10,
                produceSoldAmount = 1,
            ),
            ProduceQuota(
                produceName = "Mango",
                produceGoalAmount = 5,
                produceSoldAmount = 0,
            ),
        )
    ), Quota(
        marketName = "St. Catherines Market",
        produceQuotaList = listOf(
            ProduceQuota(
                produceName = "Banana",
                produceGoalAmount = 5,
                produceSoldAmount = 2,
            ),
            ProduceQuota(
                produceName = "Mango",
                produceGoalAmount = 12,
                produceSoldAmount = 3,
            ),
        )
    ),
    )

    fun getCategorizedQuotas(): Flow<MutableList<Quota>> {
        return flow {
            emit(quotasList)
        }.flowOn(Dispatchers.IO)
    }
}