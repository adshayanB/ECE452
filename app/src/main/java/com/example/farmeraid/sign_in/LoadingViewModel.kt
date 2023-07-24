package com.example.farmeraid.sign_in

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.sign_in.model.SignInModel
import com.example.farmeraid.sign_in.model.getSignInButton
import com.example.farmeraid.snackbar.SnackbarDelegate
import com.example.farmeraid.uicomponents.models.UiComponentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
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


    fun login() = viewModelScope.launch {
        val result: SignInModel.AuthResponse = userRepository.checkLoggedIn()

        when (result) {
            is SignInModel.AuthResponse.Success -> {
                Log.d("MESSAGE", "LOGGED IN")
                val id = userRepository.getUserId()?.let { Log.d("UserID", it) }
                if (id == null){
                    appNavigator.navigateToSignIn()
                }
                val farmId = userRepository.getFarmId().toString()
                Log.d("FARM ID", farmId)

                if (farmId == "none") {
                    appNavigator.navigateToFarmSelection()
                } else {
                    appNavigator.navigateToMode(NavRoute.Home)
                }
            }

            is SignInModel.AuthResponse.Error -> {
                Log.d("MESSAGE", result.error)
            }
        }

    }


}