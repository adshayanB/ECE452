package com.example.farmeraid.sign_up

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.sign_in.model.SignInModel
import com.example.farmeraid.sign_in.model.getSignInButton
import com.example.farmeraid.sign_up.model.SignUpModel
import com.example.farmeraid.sign_up.model.getSignUpButton
import com.example.farmeraid.snackbar.SnackbarDelegate
import com.example.farmeraid.uicomponents.models.UiComponentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
) : ViewModel() {
    private val _state = MutableStateFlow(
        SignUpModel.SignUpViewState(
        buttonUiState = getSignUpButton()
    ))
    val state: StateFlow<SignUpModel.SignUpViewState>
        get() = _state


    private val username: MutableStateFlow<String> = MutableStateFlow(_state.value.userName)
    private val password: MutableStateFlow<String> = MutableStateFlow(_state.value.passWord)
    private val name: MutableStateFlow<String> = MutableStateFlow(_state.value.name)

    private val buttonUiState: MutableStateFlow<UiComponentModel.ButtonUiState> = MutableStateFlow(_state.value.buttonUiState)

    init {
        viewModelScope.launch {
            combine(username, password, name, buttonUiState) {
                    userName: String, password: String, name:String,  buttonUiState: UiComponentModel.ButtonUiState ->
                SignUpModel.SignUpViewState(
                    name = name,
                    userName = userName,
                    passWord = password,
                    buttonUiState = buttonUiState,
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun signup(userName: String, password: String) = viewModelScope.launch {
        buttonUiState.value = buttonUiState.value.copy(isLoading = true)
        val result: SignInModel.AuthResponse = userRepository.signup(userName, password)
        buttonUiState.value = buttonUiState.value.copy(isLoading = false)

        when(result) {
            is SignInModel.AuthResponse.Success -> {
                Log.d("MESSAGE", "LOGGED IN")
                appNavigator.navigateToMode(NavRoute.SignIn)
            }
            is SignInModel.AuthResponse.Error -> {
                Log.d("MESSAGE", result.error)
                snackbarDelegate.showSnackbar(
                    message = result.error
                )
            }
        }

    }

    fun moveToSignIn(){
        appNavigator.navigateToMode(NavRoute.SignIn)
    }

}