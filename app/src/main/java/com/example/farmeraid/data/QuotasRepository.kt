package com.example.farmeraid.data

import com.example.farmeraid.home.model.HomeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class QuotasRepository {
    fun getCategorizedQuotas(): Flow<List<HomeModel.Quota>> {
        return flow {
            val quotasList = listOf(HomeModel.Quota(
                marketName = "St. Jacob's",
                produceQuotaList = listOf(
                    HomeModel.ProduceQuota(
                        produceName = "Apple",
                        produceGoalAmount = 25,
                        produceSoldAmount = 10,
                    ),
                    HomeModel.ProduceQuota(
                        produceName = "Bananas",
                        produceGoalAmount = 10,
                        produceSoldAmount = 9,
                    ),
                )
            ), HomeModel.Quota(
                marketName = "St. Lawrence",
                produceQuotaList = listOf(
                    HomeModel.ProduceQuota(
                        produceName = "Carrots",
                        produceGoalAmount = 15,
                        produceSoldAmount = 10,
                    ),
                    HomeModel.ProduceQuota(
                        produceName = "Oranges",
                        produceGoalAmount = 24,
                        produceSoldAmount = 7,
                    ),
                )
            ), HomeModel.Quota(
                marketName = "Another one",
                produceQuotaList = listOf(
                    HomeModel.ProduceQuota(
                        produceName = "Apples",
                        produceGoalAmount = 4,
                        produceSoldAmount = 3,
                    ),
                    HomeModel.ProduceQuota(
                        produceName = "Bananas",
                        produceGoalAmount = 10,
                        produceSoldAmount = 1,
                    ),
                    HomeModel.ProduceQuota(
                        produceName = "Pears",
                        produceGoalAmount = 5,
                        produceSoldAmount = 0,
                    ),
                )
            ), HomeModel.Quota(
                marketName = "Another one",
                produceQuotaList = listOf(
                    HomeModel.ProduceQuota(
                        produceName = "Apples",
                        produceGoalAmount = 4,
                        produceSoldAmount = 3,
                    ),
                    HomeModel.ProduceQuota(
                        produceName = "Bananas",
                        produceGoalAmount = 10,
                        produceSoldAmount = 1,
                    ),
                    HomeModel.ProduceQuota(
                        produceName = "Pears",
                        produceGoalAmount = 5,
                        produceSoldAmount = 0,
                    ),
                )
            ))
            emit(quotasList)
        }.flowOn(Dispatchers.IO)
    }
}