package com.example.farmeraid.data.model

class QuotaModel {

    data class ProduceQuota(
        val produceName: String,
        val produceGoalAmount: Int,
        val saleAmount: Int
    )

    data class Quota(
        val id: String,
        val produceQuotaList: List<ProduceQuota>
    )
}