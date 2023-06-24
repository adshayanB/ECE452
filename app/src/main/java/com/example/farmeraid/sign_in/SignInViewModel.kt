package com.example.farmeraid.sign_in

import com.example.farmeraid.snackbar.SnackbarDelegate
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.sign_in.model.SignInModel
import com.example.farmeraid.sign_in.model.getSignInButton

import androidx.lifecycle.viewModelScope
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.uicomponents.models.UiComponentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
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

        when(result) {
            is SignInModel.AuthResponse.Success -> {
                Log.d("MESSAGE", "LOGGED IN")
                appNavigator.navigateToMode(NavRoute.Home)
            }
            is SignInModel.AuthResponse.Error -> {
                Log.d("MESSAGE", result.error)
                snackbarDelegate.showSnackbar(
                    message = result.error
                )
            }
        }

    }

}