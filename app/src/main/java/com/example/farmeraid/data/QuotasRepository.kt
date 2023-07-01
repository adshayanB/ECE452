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
        val marketName : String,
        val produceQuotaList : List<ProduceQuota>,
    )

    private val quotasList: MutableList<Quota> = mutableListOf(Quota(
        marketName = "St. Jacob's",
        produceQuotaList = listOf(
            ProduceQuota(
                produceName = "Apple",
                produceGoalAmount = 25,
            ),
            ProduceQuota(
                produceName = "Banana",
                produceGoalAmount = 10,
            ),
        )
    ), Quota(
        marketName = "St. Lawrence",
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
        marketName = "Kenzington Market",
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
        marketName = "St. Catherines Market",
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

    fun getQuota(market : MarketModel.Market): Quota? {
        return quotasList.firstOrNull { quota -> quota.marketName == market.name }
    }

    fun addQuota(market : MarketModel.Market, produce : List<ProduceQuota>) {
        val quotaIndex : Int = quotasList.indexOfFirst { quota -> quota.marketName == market.name }

        if (quotaIndex >= 0) {
            quotasList[quotaIndex] = Quota(
                marketName = market.name,
                produceQuotaList = produce,
            )
        } else {
            quotasList.add(Quota(
                marketName = market.name,
                produceQuotaList = produce,
            ))
        }
    }
}