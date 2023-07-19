package com.example.farmeraid.data

import android.util.Log
import com.example.farmeraid.data.model.FarmModel
import com.example.farmeraid.data.model.ResponseModel
import com.example.farmeraid.data.model.UserModel
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class FarmRepository(
    val farmCode: MutableStateFlow<String> = MutableStateFlow(""),
    private val userRepository: UserRepository
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

                //Set code state
                farmCode.value = code

                db.collection("inventory").document(docRef.id).set(
                    mapOf("produce" to emptyMap<String,Int>())
                )

                //TODO create new transaction document for the farm

                 db.collection("users").document(userRepository.getUserId()!!).update(mapOf("farmID" to docRef.id, "admin" to true)).await()

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
                    ref.whereEqualTo("code", farmCode).get().await().let{ snap ->
                        db.collection("farm").document(snap.documents[0].id).update(
                            "users", FieldValue.arrayUnion(id)
                        )
                    }
                }
                //TODO update userRespository with new farmID
                //TODO Function needs to be made on UserRepo
            }

            ResponseModel.FAResponse.Success
        }catch (e: Exception){
            return ResponseModel.FAResponse.Error(e.message?:"Error joining a farm. Please try again.")
//            Log.e("FarmRepository - joinFarm()", e.message?:"Unknown error")
        }

    }

    suspend fun getMarketIds (): ResponseModel.FAResponseWithData<MutableList<String>> {
        Log.d("TEST", "called")
        return (userRepository.getFarmId()?.let { id ->
            try {
                db.collection("farm").document(id)
                    .get()
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

    suspend fun getTransactionIds (): ResponseModel.FAResponseWithData<MutableList<String>> {
        Log.d("TEST", "called")
        return (userRepository.getFarmId()?.let { id ->
            try {
                db.collection("farm").document(id)
                    .get()
                    .await()
                    .data?.get("transactions")?.let {
                        ResponseModel.FAResponseWithData.Success(it as MutableList<String>)
                    } ?: ResponseModel.FAResponseWithData.Error("Error fetching transactions")
            } catch (e : Exception) {
                Log.e("InventoryRepository", e.message ?: e.stackTraceToString())
                ResponseModel.FAResponseWithData.Error(e.message ?: "Unknown error while getting transactions")
            }
        } ?: ResponseModel.FAResponseWithData.Error("User is not part of a farm"))
    }
    //Taken from: https://stackoverflow.com/questions/46943860/idiomatic-way-to-generate-a-random-alphanumeric-string-in-kotlin
    fun generateFarmCode(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun getFarmCode(): String? {
        return farmCode.value
    }
}