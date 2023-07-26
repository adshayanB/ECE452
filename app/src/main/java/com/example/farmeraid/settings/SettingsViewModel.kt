package com.example.farmeraid.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.FarmRepository
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.settings.model.SettingsModel
import com.example.farmeraid.settings.model.getSignOutButton
import com.example.farmeraid.uicomponents.models.UiComponentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appNavigator : AppNavigator,
    private val farmRepository: FarmRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsModel.SettingsViewState(
        buttonUiState = getSignOutButton()
    ))

    val state: StateFlow<SettingsModel.SettingsViewState>
        get() = _state


    private val buttonUiState: MutableStateFlow<UiComponentModel.ButtonUiState> = MutableStateFlow(_state.value.buttonUiState)
    private val code: MutableStateFlow<String> = MutableStateFlow("")

    init {
        viewModelScope.launch {
            combine(buttonUiState, code) {
                    buttonUiState: UiComponentModel.ButtonUiState, code: String->
                SettingsModel.SettingsViewState(
                    buttonUiState = buttonUiState,
                    code = code
                )
            }.collect {
                _state.value = it
            }
        }
    }
    init{
        viewModelScope.launch {
            code.value = farmRepository.getFarmCode()!!
        }
    }

    fun navigateToSignOut() {
        appNavigator.navigateToSignOut()
    }
}