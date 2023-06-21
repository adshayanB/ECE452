package com.example.farmeraid.sign_in

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.sign_in.model.SignInModel
import com.example.farmeraid.sign_in.model.getSignInButton

import androidx.lifecycle.viewModelScope
import com.example.farmeraid.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(SignInModel.SignInViewState(
        buttonUiState = getSignInButton()
    ))
    val state: StateFlow<SignInModel.SignInViewState>
        get() = _state


    private val username: MutableStateFlow<String> = MutableStateFlow(_state.value.userName)
    private val password: MutableStateFlow<String> = MutableStateFlow(_state.value.passWord)
    private val loggedIn: MutableStateFlow<String> = MutableStateFlow(_state.value.loggedIn)


    init {
        viewModelScope.launch {
            combine(username, password) {
                    userName: String, password: String ->
                SignInModel.SignInViewState(
                    userName = userName,
                    passWord = password,
                    buttonUiState = getSignInButton(),
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun login(userName: String, password: String) {
        //Code to login
        Log.v("MESSAGE", password)
        Log.v("MESSAGE", userName)
        lateinit var firebaseAuth: FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        if (userName.isNotEmpty() && password.isNotEmpty()) {

            firebaseAuth.signInWithEmailAndPassword(userName, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.v("RUNS", "LOGGEd IN")
                        loggedIn.value = "True"

//                    val intent = Intent(this, MainActivity::class.java)
//                    startActivity(intent)
                } else {
                    loggedIn.value = "False"
                        Log.v("GG", "Did not wor")

                    }
                }
//        } else {
//            Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
//
//        }
        }
    }
}