package com.example.farmeraid.data

import com.example.farmeraid.data.model.UserModel
import com.example.farmeraid.sign_in.model.SignInModel
import com.example.farmeraid.sign_up.model.SignUpModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

class UserRepository {
    val user: MutableStateFlow<UserModel.User?> = MutableStateFlow(null)
    suspend fun login(userName: String, password: String) : SignInModel.AuthResponse {
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        if (userName.isNotEmpty() && password.isNotEmpty()) {
            return try {
                val res = firebaseAuth.signInWithEmailAndPassword(userName, password).await()
                 user.value = UserModel.User(email = userName, id = firebaseAuth.currentUser?.uid.toString())
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

    suspend fun signup(userName: String, password: String) : SignUpModel.AuthResponse {
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        if (userName.isNotEmpty() && password.isNotEmpty()) {
            return try {
                val res = firebaseAuth.createUserWithEmailAndPassword(userName, password).await()
                SignUpModel.AuthResponse.Success
            } catch (e: Exception) {
                return SignUpModel.AuthResponse.Error(
                    e.message ?: "Error signing up. Please try again later."
                )
            }
        }
        else{
            return SignUpModel.AuthResponse.Error("Fields cannot be empty")
        }
    }

    fun getUserId():String?{
        return user.value?.id
    }

    fun getUserEmail():String?{
        return user.value?.email
    }
}