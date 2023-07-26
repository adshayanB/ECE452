package com.example.farmeraid.data

import com.example.farmeraid.data.model.UserModel
import com.example.farmeraid.data.source.NetworkMonitor
import com.example.farmeraid.sign_in.model.SignInModel
import com.example.farmeraid.sign_up.model.SignUpModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val networkMonitor: NetworkMonitor,
) {
    val user: MutableStateFlow<UserModel.User?> = MutableStateFlow(null)
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    suspend fun login(userName: String, password: String) : SignInModel.AuthResponse {
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

        if  (userName.isNotEmpty() && password.isNotEmpty()) {
            return try {
                val res = firebaseAuth.signInWithEmailAndPassword(userName, password).await()
                //Get user from user object
                val docRef = db.collection("users").document(firebaseAuth.currentUser?.uid.toString())
                val data = docRef.get(networkMonitor.getSource()).await().data
                val farmID = data?.get("farmID").toString()
                val admin = data?.get("admin") as Boolean
                user.value = UserModel.User(
                    email = userName,
                    id = firebaseAuth.currentUser?.uid.toString(),
                    farm_id = farmID,
                    admin = admin,
                )
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

    suspend fun checkLoggedIn():SignInModel.AuthResponse {
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null) {
            return try {
                //Get user from user object
                val docRef =
                    db.collection("users").document(firebaseAuth.currentUser?.uid.toString())
                val data = docRef.get(networkMonitor.getSource()).await().data
                val farmID = data?.get("farmID").toString()
                val admin = data?.get("admin") as Boolean
                user.value = firebaseAuth.currentUser!!.email?.let {
                    UserModel.User(
                        email = it,
                        id = firebaseAuth.currentUser?.uid.toString(),
                        farm_id = farmID,
                        admin = admin,
                    )
                }
                SignInModel.AuthResponse.Success
            } catch (e: Exception) {
                return SignInModel.AuthResponse.Error(
                    e.message ?: "Error logging in. Please try again later."
                )
            }

        }
        else{
            return SignInModel.AuthResponse.Success
        }
    }
    fun signOut(): SignInModel.AuthResponse {
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        return try {
            firebaseAuth.signOut()
            SignInModel.AuthResponse.Success
        } catch (e: Exception) {
            return SignInModel.AuthResponse.Error(
                e.message ?: "Error logging in. Please try again later."
            )
        }
    }
    suspend fun signup(userName: String, password: String) : SignUpModel.AuthResponse {
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        if (userName.isNotEmpty() && password.isNotEmpty()) {
            return try {
                val res = firebaseAuth.createUserWithEmailAndPassword(userName, password).await()
                val docRef = res.user?.let {
                    db.collection("users").document(it.uid).set(
                        mapOf (
                            "admin" to false,
                            "farmID" to "none"
                        )
                    )
                }
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

    suspend fun updateFarmId(): SignInModel.AuthResponse {
        return try {
            val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
            val docRef = db.collection("users").document(firebaseAuth.currentUser?.uid.toString())
            val data = docRef.get(networkMonitor.getSource()).await().data
            val farmID = data?.get("farmID").toString()
            val admin = data?.get("admin") as Boolean
            user.value = getUserEmail()?.let {
                UserModel.User(
                    email = it,
                    id = firebaseAuth.currentUser?.uid.toString(),
                    farm_id = farmID,
                    admin = admin,
                )
            }
            SignInModel.AuthResponse.Success
        } catch (e: Exception) {
            return SignInModel.AuthResponse.Error(
                e.message ?: "Error updating"
            )
        }
    }

    fun getFarmId(): String? {
        return user.value?.farm_id

    }

    fun isAdmin(): Boolean {
        return user.value?.admin ?: false
    }
}