package com.example.farmeraid.data

import com.example.farmeraid.home.model.HomeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class QuotasRepository {
    fun getCategorizedQuotas(): Flow<List<HomeModel.CategorizedQuotas>> {
        return flow {
            val quotasList = listOf(HomeModel.CategorizedQuotas(
                marketName = "test",
                quotas = listOf(HomeModel.Quota(
                    produceName = "Test Produce",
                    produceGoalAmount = 25,
                    produceSoldAmount = 10,
                ))
            ))
            emit(quotasList)
        }.flowOn(Dispatchers.IO)
    }
}