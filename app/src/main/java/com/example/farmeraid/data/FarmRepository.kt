package com.example.farmeraid.data

import android.util.Log
import com.example.farmeraid.data.model.FarmModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FarmRepository(
    private val userRepository: UserRepository
) {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun createFarm(farmName: String){
        //if db.collection("farm").get
        try {
            userRepository.getUserId()?.let{
                val docRef = db.collection("farm").add(
                    mapOf (
                        "name" to farmName,
                        "users" to listOf(it),
                        "markets" to emptyList<String>(),
                        "charities" to emptyList<String>()
                    )
                ).await()

                db.collection("inventory").document(docRef.id).set(
                    mapOf("produce" to emptyMap<String,Int>())
                )

                //TODO create new transaction document for the farm

                //TODO update userRespository with new farmID
                //TODO Function needs to be made on UserRepo

            }
        }catch(e: Exception){
            Log.e("FarmRepository - createFarm()", e.message?:"Unknown error")
        }




    }
    suspend fun joinFarm(farmCode: String){
        try {
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
        }catch (e: Exception){
            Log.e("FarmRepository - joinFarm()", e.message?:"Unknown error")
        }

    }


}