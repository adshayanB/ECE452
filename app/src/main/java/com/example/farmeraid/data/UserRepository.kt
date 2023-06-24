package com.example.farmeraid.data

import android.util.Log
import com.example.farmeraid.sign_in.model.SignInModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class UserRepository {
    suspend fun login(userName: String, password: String) : SignInModel.AuthResponse {
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        if (userName.isNotEmpty() && password.isNotEmpty()) {
            return try {
                val res = firebaseAuth.signInWithEmailAndPassword(userName, password).await()
                SignInModel.AuthResponse.Success
            }
            catch (e:Exception){
                return SignInModel.AuthResponse.Error(e.message ?:
                "Error logging in. Please try again later.")
            }
    }
        else{
            return SignInModel.AuthResponse.Error("Fields cannot be empty")
        }
    }
}