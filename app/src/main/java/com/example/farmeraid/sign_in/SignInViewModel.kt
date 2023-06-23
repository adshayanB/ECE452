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
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.uicomponents.models.UiComponentModel
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
    private val userRepository: UserRepository,
    private val appNavigator: AppNavigator
) : ViewModel() {
    private val _state = MutableStateFlow(SignInModel.SignInViewState(
        buttonUiState = getSignInButton()
    ))
    val state: StateFlow<SignInModel.SignInViewState>
        get() = _state


    private val username: MutableStateFlow<String> = MutableStateFlow(_state.value.userName)
    private val password: MutableStateFlow<String> = MutableStateFlow(_state.value.passWord)
    private val buttonUiState: MutableStateFlow<UiComponentModel.ButtonUiState> = MutableStateFlow(_state.value.buttonUiState)



    init {
        viewModelScope.launch {
            combine(username, password, buttonUiState) {
                    userName: String, password: String, buttonUiState: UiComponentModel.ButtonUiState ->
                SignInModel.SignInViewState(
                    userName = userName,
                    passWord = password,
                    buttonUiState = buttonUiState,
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun login(userName: String, password: String) = viewModelScope.launch {
        buttonUiState.value = buttonUiState.value.copy(isLoading = true)
        val result: SignInModel.AuthResponse = userRepository.login(userName, password)
        buttonUiState.value = buttonUiState.value.copy(isLoading = false)
        if (result is SignInModel.AuthResponse.Success) {
            Log.d("MESSAGE", "LOGGED IN")
            appNavigator.navigateToMode(NavRoute.Home)
        } else {
            Log.d("MESSAGE", result.toString())
        }

        }

}