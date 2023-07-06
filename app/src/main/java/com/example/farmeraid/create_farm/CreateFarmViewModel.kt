package com.example.farmeraid.create_farm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.create_farm.model.CreateFarmModel
import com.example.farmeraid.create_farm.model.getSubmitButton
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFarmViewModel @Inject constructor(
    private val farmRepository: FarmRepository,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
) : ViewModel() {
    private val _state = MutableStateFlow(CreateFarmModel.CreateFarmViewState(
        buttonUiState = getSubmitButton()
    ))

    val state: StateFlow<CreateFarmModel.CreateFarmViewState>
        get() = _state

    private val farmName: MutableStateFlow<String> = MutableStateFlow(_state.value.farmName)
    private val buttonUiState: MutableStateFlow<UiComponentModel.ButtonUiState> = MutableStateFlow(_state.value.buttonUiState)

    init {
        viewModelScope.launch {
            combine(farmName, buttonUiState) {
                    farmName: String, buttonUiState: UiComponentModel.ButtonUiState ->
                CreateFarmModel.CreateFarmViewState(
                    farmName = farmName,
                    buttonUiState = buttonUiState,
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun submitFarm() = viewModelScope.launch {
        buttonUiState.value = buttonUiState.value.copy(isLoading = true)
        val result: ResponseModel.FAResponse = farmRepository.createFarm(farmName.value)
        buttonUiState.value = buttonUiState.value.copy(isLoading = false)

        when (result) {
            is ResponseModel.FAResponse.Success -> {
                Log.d("MESSAGE - createFarm()", "SUCCESSFULLY CREATED A FARM")
                appNavigator.navigateToFarmCode()
            }

            is ResponseModel.FAResponse.Error -> {
                Log.e("ERROR - createFarm()", result.error?:"Unknown error")
                snackbarDelegate.showSnackbar(
                    message = result.error?:"Unknown error"
                )
            }
        }
    }

    fun setFarmName(newVal: String) {
        farmName.value = newVal
    }

    fun navigateBack() {
        appNavigator.navigateBack()
    }
}