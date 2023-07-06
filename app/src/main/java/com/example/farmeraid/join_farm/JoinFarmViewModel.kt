package com.example.farmeraid.join_farm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.join_farm.model.JoinFarmModel
import com.example.farmeraid.join_farm.model.getSubmitButton
import com.example.farmeraid.data.FarmRepository
import com.example.farmeraid.data.model.ResponseModel
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.snackbar.SnackbarDelegate
import com.example.farmeraid.uicomponents.models.UiComponentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinFarmViewModel @Inject constructor(
    private val farmRepository: FarmRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
) : ViewModel() {
    private val _state = MutableStateFlow(JoinFarmModel.JoinFarmViewState(
        buttonUiState = getSubmitButton()
    ))

    val state: StateFlow<JoinFarmModel.JoinFarmViewState>
        get() = _state

    private val farmCode: MutableStateFlow<String> = MutableStateFlow(_state.value.farmCode)
    private val buttonUiState: MutableStateFlow<UiComponentModel.ButtonUiState> = MutableStateFlow(_state.value.buttonUiState)

    init {
        viewModelScope.launch {
            combine(farmCode, buttonUiState) {
                    farmName: String, buttonUiState: UiComponentModel.ButtonUiState ->
                JoinFarmModel.JoinFarmViewState(
                    farmCode = farmName,
                    buttonUiState = buttonUiState,
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun submitFarmCode() = viewModelScope.launch {
        buttonUiState.value = buttonUiState.value.copy(isLoading = true)
        //val result: ResponseModel.FAResponse = farmRepository.joinFarm(farmCode.value)
        buttonUiState.value = buttonUiState.value.copy(isLoading = false)

        snackbarDelegate.showSnackbar(
            message = "Navigates to Home"
        )

//        when (result) {
//            is ResponseModel.FAResponse.Success -> {
//                Log.d("MESSAGE - joinFarm()", "SUCCESSFULLY CREATED A FARM")
//                appNavigator.navigateToMode(NavRoute.Home)
//            }
//
//            is ResponseModel.FAResponse.Error -> {
//                Log.e("ERROR - joinFarm()", result.error?:"Unknown error")
//                snackbarDelegate.showSnackbar(
//                    message = result.error?:"Unknown error"
//                )
//            }
//        }
    }

    fun setFarmCode(newVal: String) {
        farmCode.value = newVal
    }

    fun navigateBack() {
        appNavigator.navigateBack()
    }
}