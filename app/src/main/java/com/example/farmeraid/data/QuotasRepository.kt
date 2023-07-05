package com.example.farmeraid.data

import android.util.Log
import com.example.farmeraid.data.model.MarketModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

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
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

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

    suspend fun getQuota(id : String): Quota? {
        val docRef = db.collection("quotas").document(id)

        val quotas = docRef?.get()?.await()?.data?.get("produce")
        val produceList: MutableList<ProduceQuota> = mutableListOf()

        if (quotas != null) {
            val quotasMap = quotas as MutableMap<String, Int>
            for ((key, value) in quotasMap) {
                var produceQuota = ProduceQuota(
                    produceName = key,
                    produceGoalAmount = value
                )

                produceList.add(produceQuota)
            }
            return Quota(
                id = id,
                produceQuotaList = produceList
            )
        } else{
           return null
        }
//        return quotasList.firstOrNull { quota -> quota.id == id }
    }

    fun addQuota(market : MarketModel.Market, produce : List<ProduceQuota>){
            val produceQuota = mutableMapOf<String, Int>()

            for (prod in produce){
                produceQuota[prod.produceName] = prod.produceGoalAmount
            }

            val data = hashMapOf(
                "produce" to produceQuota
            )
        db.collection("quotas").document(market.id)
            .set(data)
            .addOnSuccessListener { Log.d("Success", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("Error", "Error writing document", e) }
    }
}