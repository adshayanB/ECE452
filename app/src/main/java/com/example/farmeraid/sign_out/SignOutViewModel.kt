package com.example.farmeraid.sign_in

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.sign_in.model.SignInModel
import com.example.farmeraid.snackbar.SnackbarDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignOutViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
) : ViewModel() {
//    private val _state = MutableStateFlow(SignInModel.SignInViewState(
//        buttonUiState = getSignInButton()
//    ))
//    val state: StateFlow<SignInModel.SignInViewState>
//        get() = _state

    init {
        viewModelScope.launch {
        }
    }


    fun logout() = viewModelScope.launch {
        val result: SignInModel.AuthResponse = userRepository.signOut()

        when (result) {
            is SignInModel.AuthResponse.Success -> {
                appNavigator.navigateToSignIn()
            }
            is SignInModel.AuthResponse.Error -> {
                Log.d("MESSAGE", result.error)
            }
        }

    }


}