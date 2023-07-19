package com.example.farmeraid.create_farm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.create_farm.model.FarmCodeModel
import com.example.farmeraid.create_farm.model.getStartButton
import com.example.farmeraid.data.FarmRepository
import com.example.farmeraid.data.TransactionRepository
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
class FarmCodeViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val farmRepository: FarmRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(FarmCodeModel.FarmCodeViewState(
        buttonUiState = getStartButton()
    ))

    val state: StateFlow<FarmCodeModel.FarmCodeViewState>
        get() = _state


    private val buttonUiState: MutableStateFlow<UiComponentModel.ButtonUiState> = MutableStateFlow(_state.value.buttonUiState)
    private val isLoading : MutableStateFlow<Boolean> = MutableStateFlow(_state.value.isLoading)
    private val code: MutableStateFlow<String> = MutableStateFlow("")

    init {
        viewModelScope.launch {
            combine(buttonUiState, isLoading, code) {
                    buttonUiState: UiComponentModel.ButtonUiState, isLoading : Boolean, code: String->
                FarmCodeModel.FarmCodeViewState(
                    buttonUiState = buttonUiState,
                    isLoading = isLoading,
                    code = code
                )
            }.collect {
                _state.value = it
            }
        }
    }
    init{
        viewModelScope.launch {
            code.value = farmRepository.getFarmCode().toString()
        }
    }

    fun navigateToHome() {
        appNavigator.navigateToMode(NavRoute.Home)
    }

    // TODO: create a function to get the RNG Farm Code from Firebase
}