package com.example.farmeraid.data

import android.util.Log
import com.example.farmeraid.data.model.CharityModel
import com.example.farmeraid.data.model.FridgeModel
import com.example.farmeraid.data.model.ResponseModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CharityRepository (
    private val farmRepository: FarmRepository,
    private val userRepository: UserRepository
){
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun createCharity(charityName: String, location:String, produce: List<QuotasRepository.ProduceQuota>): ResponseModel.FAResponse{
        return try {

            userRepository.getFarmId()?.let{
                val docRef = db.collection("charity").add(
                    mapOf (
                        "charityName" to charityName,
                        "location" to location,
                        "produce" to produce.associate {
                            it.produceName to it.produceGoalAmount
                        }

                    )
                ).await()

                //Update charity id to farm list
                userRepository.getFarmId()
                    ?.let { db.collection("farm").document(it).update("charities", FieldValue.arrayUnion(docRef.id)).await() }

                //TODO create new transaction document for the farm
                ResponseModel.FAResponse.Success
            } ?: ResponseModel.FAResponse.Error("User does not exist")
        }catch(e: Exception){
            return ResponseModel.FAResponse.Error(e.message?:"Error creating a charity. Please try again.")
            //Log.e("FarmRepository - createFarm()", e.message?:"Unknown error")
        }
    }

    suspend fun getCharity(id: String): ResponseModel.FAResponseWithData<FridgeModel.Fridge> {
        val docRead : DocumentSnapshot
        try {
            docRead = db.collection("charity").document(id).get().await()
        } catch (e : Exception) {
            Log.e("Charity Repository",e.message ?: e.stackTraceToString())
            return ResponseModel.FAResponseWithData.Error(e.message ?: "Unknown error while getting charities")
        }

        if (!docRead.exists()) {
            return ResponseModel.FAResponseWithData.Error("Charity does not exist")
        }

        val items = docRead.data?.get("produce")
        val name  = docRead.data?.get("charityName")
        val location = docRead.data?.get("location")

        return if (items != null && name !=null && location!=null) {
            val itemsMap = items as MutableMap<String, Int>
            val fridgeName = name as String
            val charityLocation = location as String
            ResponseModel.FAResponseWithData.Success(
                FridgeModel.Fridge(
                    fridgeName = fridgeName,
                    location = charityLocation,
                    items = itemsMap.map { (produceName, goal) ->
                        CharityModel.ProduceFridge(
                            produceName = produceName,
                            produceDonateAmount = goal,

                            )
                    }
                )
            )
        }
        else {
            ResponseModel.FAResponseWithData.Error("Charity object does not have produce or name or location information")
        }
    }

}