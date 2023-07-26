package com.example.farmeraid.data

import android.util.Log
import com.example.farmeraid.fridge.model.FridgeModel
import com.example.farmeraid.data.model.QuotaModel
import com.example.farmeraid.data.model.ResponseModel
import com.example.farmeraid.location_provider.LocationProvider
import com.example.farmeraid.data.source.NetworkMonitor
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await

class CharityRepository (
    private val farmRepository: FarmRepository,
    private val userRepository: UserRepository,
    private val networkMonitor: NetworkMonitor,
){
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun createCharity(charityName: String, location:String, coordinates: GeoPoint, imageUri: String, handle: String): ResponseModel.FAResponse{
        return try {

            userRepository.getFarmId()?.let{
                val docRef = db.collection("charity").add(
                    mapOf (
                        "charityName" to charityName,
                        "location" to location,
                        "coordinates" to coordinates,
                        "imageUri" to imageUri,
                        "handle" to handle,

                    )
                ).await()

                //Update charity id to farm list
                userRepository.getFarmId()
                    ?.let { db.collection("farm").document(it).update("charities", FieldValue.arrayUnion(docRef.id)) }

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
            docRead = db.collection("charity").document(id).get(networkMonitor.getSource()).await()
        } catch (e : Exception) {
            Log.e("Charity Repository",e.message ?: e.stackTraceToString())
            return ResponseModel.FAResponseWithData.Error(e.message ?: "Unknown error while getting charities")
        }

        if (!docRead.exists()) {
            return ResponseModel.FAResponseWithData.Error("Charity does not exist")
        }

        val imageLink = docRead.data?.get("imageUri") as String?
        val handle = docRead.data?.get("handle") as String?
        val name  = docRead.data?.get("charityName") as String?
        val location = docRead.data?.get("location") as String?
        val coordinates = docRead.data?.get("coordinates") as GeoPoint?

        return if (imageLink != null && handle != null &&name !=null && location!=null && coordinates != null) {
            ResponseModel.FAResponseWithData.Success(
                FridgeModel.Fridge(
                    id = id,
                    fridgeName = name,
                    location = location,
                    handle = handle,
                    imageUri = imageLink,
                    coordinates = LocationProvider.LatandLong(
                        coordinates.latitude,
                        coordinates.longitude
                    ),
                )
            )
        }
        else {
            ResponseModel.FAResponseWithData.Error("Charity object does not have produce or name or location information")
        }
    }

    suspend fun getFridgeImages(): List<String> {
        val imageUris: List<String> = listOf(
            "https://firebasestorage.googleapis.com/v0/b/farmer-aid-452.appspot.com/o/fridge1.jpg?alt=media&token=4ea33b47-26ae-4ad6-a8bd-c8102a9d7b13",
            "https://firebasestorage.googleapis.com/v0/b/farmer-aid-452.appspot.com/o/fridge10.jpg?alt=media&token=25d27c98-f25f-49ae-b9cd-315935ab1295",
            "https://firebasestorage.googleapis.com/v0/b/farmer-aid-452.appspot.com/o/fridge2.jpg?alt=media&token=376ac091-a9c7-479e-b23b-dc4112ecf661",
            "https://firebasestorage.googleapis.com/v0/b/farmer-aid-452.appspot.com/o/fridge8.jpg?alt=media&token=629e364e-30e9-4f91-b8bd-ed2f5bb77eb0"
        )
        return (imageUris)
    }

}