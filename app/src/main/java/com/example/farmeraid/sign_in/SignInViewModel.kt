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
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.data.module.RepositoryModule.provideUserRepository
import com.example.farmeraid.home.model.HomeModel
import com.google.android.gms.common.api.Response
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SignInModel.SignInViewState(
        buttonUiState = getSignInButton()
    ))
    val state: StateFlow<SignInModel.SignInViewState>
        get() = _state


    private val username: MutableStateFlow<String> = MutableStateFlow(_state.value.userName)
    private val password: MutableStateFlow<String> = MutableStateFlow(_state.value.passWord)
    private val loggedIn: MutableStateFlow<String> = MutableStateFlow(_state.value.loggedIn)
    private val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(_state.value.isLoading)
    private val userRepository: UserRepository = userRepository;




    init {
        viewModelScope.launch {
            combine(username, password, isLoading) {
                    userName: String, password: String, isLoading:Boolean ->
                SignInModel.SignInViewState(
                    userName = userName,
                    passWord = password,
                    isLoading = isLoading,
                    buttonUiState = getSignInButton(),
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun login(userName: String, password: String) = viewModelScope.launch {
        isLoading.value = true
        val result: SignInModel.AuthResponse = userRepository.login(userName, password)
        isLoading.value = false
        if (result is SignInModel.AuthResponse.Success) {
            Log.d("MESSAGE", "LOGGED IN")
        } else {
            Log.d("MESSAGE", result.toString())
        }

        }

}