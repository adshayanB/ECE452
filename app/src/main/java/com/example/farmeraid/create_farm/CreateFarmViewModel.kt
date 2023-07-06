package com.example.farmeraid.create_farm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.create_farm.model.CreateFarmModel
import com.example.farmeraid.create_farm.model.getSubmitButton
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.snackbar.SnackbarDelegate
import com.example.farmeraid.uicomponents.models.UiComponentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFarmViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
) : ViewModel() {
    private val _state = MutableStateFlow(CreateFarmModel.CreateFarmViewState(
        buttonUiState = getSubmitButton()
    ))

    val state: StateFlow<CreateFarmModel.CreateFarmViewState>
        get() = _state

    private val farmName: MutableStateFlow<String> = MutableStateFlow(_state.value.farmName)
    private val location: MutableStateFlow<String> = MutableStateFlow(_state.value.location)
    private val buttonUiState: MutableStateFlow<UiComponentModel.ButtonUiState> = MutableStateFlow(_state.value.buttonUiState)

    init {
        viewModelScope.launch {
            combine(farmName, location, buttonUiState) {
                    farmName: String, location: String, buttonUiState: UiComponentModel.ButtonUiState ->
                CreateFarmModel.CreateFarmViewState(
                    farmName = farmName,
                    location = location,
                    buttonUiState = buttonUiState,
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun submitFarm() = viewModelScope.launch {
        buttonUiState.value = buttonUiState.value.copy(isLoading = true)
        //val result: SignInModel.AuthResponse = userRepository.login(username.value, password.value)
        snackbarDelegate.showSnackbar(
            message = "Navigate to Farm Code"
        )
        buttonUiState.value = buttonUiState.value.copy(isLoading = false)

//        when (result) {
//            is SignInModel.AuthResponse.Success -> {
//                Log.d("MESSAGE", "LOGGED IN")
//                userRepository.getUserId()?.let { Log.d("UserID", it) }
//                appNavigator.navigateToMode(NavRoute.Home)
//            }
//
//            is SignInModel.AuthResponse.Error -> {
//                Log.d("MESSAGE", result.error)
//                snackbarDelegate.showSnackbar(
//                    message = result.error
//                )
//            }
//        }
    }

    fun setFarmName(newVal: String) {
        farmName.value = newVal
    }

    fun setLocation(newVal: String) {
        location.value = newVal
    }

    fun navigateToFarmCode() {
        appNavigator.navigateToFarmCode()
    }

    fun navigateBack() {
        //appNavigator.navigateBack()
        snackbarDelegate.showSnackbar(
            message = "Navigate to Farm Selection"
        )
    }
}