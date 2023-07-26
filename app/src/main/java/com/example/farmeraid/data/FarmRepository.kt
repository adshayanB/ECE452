package com.example.farmeraid.data

import android.util.Log
import com.example.farmeraid.data.model.ResponseModel
import com.example.farmeraid.data.source.NetworkMonitor
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

class FarmRepository(
    private val userRepository: UserRepository,
    private val networkMonitor: NetworkMonitor,
) {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun createFarm(farmName: String) : ResponseModel.FAResponse {
        return try {
            val code = generateFarmCode(6)
            userRepository.getUserId()?.let{
                val docRef = db.collection("farm").add(
                    mapOf (
                        "name" to farmName,
                        "users" to listOf(it),
                        "markets" to emptyList<String>(),
                        "charities" to emptyList<String>(),
                        "farmCode" to code
                    )
                ).await()

                db.collection("inventory").document(docRef.id).set(
                    mapOf("produce" to emptyMap<String,Int>())
                )

                //TODO create new transaction document for the farm

                 db.collection("users").document(userRepository.getUserId()!!).update(mapOf("farmID" to docRef.id, "admin" to true))

                ResponseModel.FAResponse.Success
            } ?: ResponseModel.FAResponse.Error("User does not exist")
        }catch(e: Exception){
            return ResponseModel.FAResponse.Error(e.message?:"Error creating a farm. Please try again.")
            //Log.e("FarmRepository - createFarm()", e.message?:"Unknown error")
        }
    }


    suspend fun joinFarm(farmCode: String) : ResponseModel.FAResponse {
        return try {
            userRepository.getUserId()?.let{id ->
                db.collection("farm").let{ ref ->
                    ref.whereEqualTo("farmCode", farmCode).get(networkMonitor.getSource()).await().let{ snap ->
                        db.collection("farm").document(snap.documents[0].id).update(
                            "users", FieldValue.arrayUnion(id)
                        )
                        //Update user with farmID
                        db.collection("users").document(userRepository.getUserId()!!).update(mapOf("farmID" to snap.documents[0].id, "admin" to false))
                    }
                }
            }

            ResponseModel.FAResponse.Success
        }catch (e: Exception){
            return ResponseModel.FAResponse.Error("Error joining a farm. Please ensure the code is correct.")
//            Log.e("FarmRepository - joinFarm()", e.message?:"Unknown error")
        }

    }
    suspend fun getMarketIds (): ResponseModel.FAResponseWithData<MutableList<String>> {
        Log.d("getMarketIds", "called")
        return (userRepository.getFarmId()?.let { id ->
            try {
                db.collection("farm").document(id)
                    .get(networkMonitor.getSource())
                    .await()
                    .data?.get("markets")?.let {
                        ResponseModel.FAResponseWithData.Success(it as MutableList<String>)
                    } ?: ResponseModel.FAResponseWithData.Error("Error fetching markets")
            } catch (e : Exception) {
                Log.e("InventoryRepository", e.message ?: e.stackTraceToString())
                ResponseModel.FAResponseWithData.Error(e.message ?: "Unknown error while getting inventory")
            }
        } ?: ResponseModel.FAResponseWithData.Error("User is not part of a farm"))
    }

    //Get list of charities
    suspend fun getCharityIds (): ResponseModel.FAResponseWithData<MutableList<String>> {
        Log.d("getCharityIds", "called")
        return  (userRepository.getFarmId()?.let { id ->
            try {
                db.collection("farm").document(id)
                    .get(networkMonitor.getSource())
                    .await()
                    .data?.get("charities")?.let {
                        ResponseModel.FAResponseWithData.Success(it as MutableList<String>)
                    } ?: ResponseModel.FAResponseWithData.Error("Error fetching charities")
            } catch (e : Exception) {
                Log.e("FarmRepository", e.message ?: e.stackTraceToString())
                ResponseModel.FAResponseWithData.Error(e.message ?: "Unknown error while getting charities")
            }
        } ?: ResponseModel.FAResponseWithData.Error("User is not part of a farm"))
    }

    //Taken from: https://www.techiedelight.com/generate-a-random-alphanumeric-string-in-kotlin/
    fun generateFarmCode(length: Int) : String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    suspend fun getFarmCode(): String? {
        return try {
            userRepository.getFarmId()?.let {
                db.collection("farm").document(it).get(networkMonitor.getSource()).await().data?.let{ farm ->
                    farm["farmCode"] as String
                }
            } ?: run {
                Log.e("FarmRepository", "User does ot exist")
                null
            }
        } catch (e:Exception) {
            Log.e("FarmRepository", e.message ?: "Unknown error while getting farm code")
            null
        }
    }
}