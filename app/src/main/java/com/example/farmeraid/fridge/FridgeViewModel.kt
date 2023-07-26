package com.example.farmeraid.fridge

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.CharityRepository
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.model.ResponseModel
import com.example.farmeraid.fridge.model.FridgeModel
import com.example.farmeraid.home.add_edit_produce.model.AddEditProduceModel
import com.example.farmeraid.home.add_edit_produce.model.getSubmitButton
import com.example.farmeraid.location_provider.LocationProvider
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.snackbar.SnackbarDelegate
import com.example.farmeraid.uicomponents.models.UiComponentModel
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditFridgeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val charityRepository: CharityRepository,
    private val snackbarDelegate: SnackbarDelegate,
    private val locationProvider : LocationProvider,
    private val appNavigator: AppNavigator,
) : ViewModel() {
    private val initialFridgeName: String? = savedStateHandle["fridgeName"]
    private val initialFridgeLocation : String? = savedStateHandle["fridgeLocation"]
    private val initialFridgeInstagramHandle : String? = savedStateHandle["fridgeHandle"]

    private val _state = MutableStateFlow(
        FridgeModel.AddEditFridgeViewState(
        fridgeName = initialFridgeName,
        fridgeLocation = initialFridgeLocation,
            fridgeHandle = initialFridgeInstagramHandle,
            submitButtonUiState = getSubmitButton(),
            isAddFridge = initialFridgeName == null,
    ))
    val state: StateFlow<FridgeModel.AddEditFridgeViewState>
        get() = _state

    private val fridgeName : MutableStateFlow<String?> = MutableStateFlow(_state.value.fridgeName)
    private val fridgeLocation : MutableStateFlow<String?> = MutableStateFlow(_state.value.fridgeLocation)
    private val fridgeHandle : MutableStateFlow<String?> = MutableStateFlow(_state.value.fridgeHandle)
    private val submitButtonUiState : MutableStateFlow<UiComponentModel.ButtonUiState> = MutableStateFlow(_state.value.submitButtonUiState)

    init {
        viewModelScope.launch {
            combine(fridgeName, fridgeLocation, fridgeHandle, submitButtonUiState) {
                    fridgeName: String?, fridgeLocation: String?, fridgeHandle: String?, submitButtonUiState : UiComponentModel.ButtonUiState ->
                FridgeModel.AddEditFridgeViewState(
                    fridgeName = fridgeName,
                    fridgeLocation = fridgeLocation,
                    fridgeHandle = fridgeHandle,
                    submitButtonUiState = submitButtonUiState,
                    isAddFridge = initialFridgeName == null,
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun setFridgeName(name : String?) {
        fridgeName.value = name
    }

    fun setFridgeLocation(location : String?) {
        fridgeLocation.value = location
    }

    fun setFridgeHandle(handle : String?) {
        fridgeHandle.value = handle
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getCoordinatesFromLocation(location : String) : DoubleArray {
        return locationProvider.getCoordinatesFromLocationName(location)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun submitFridge() {
        viewModelScope.launch {
            submitButtonUiState.value = submitButtonUiState.value.copy(isLoading = true)
            val fridgeName = fridgeName.value
            val fridgeLocation = fridgeLocation.value
            val fridgeHandle = fridgeHandle.value
            if (fridgeName == null) {
                snackbarDelegate.showSnackbar("Please enter a fridge name")
            } else if (fridgeLocation == null) {
                snackbarDelegate.showSnackbar("Please enter a fridge location")
            } else if (fridgeHandle == null) {
                snackbarDelegate.showSnackbar("Please enter a fridge handle")
            } else {
                val coordinatesArray = getCoordinatesFromLocation(fridgeLocation)
                val lat = coordinatesArray[0]
                val long = coordinatesArray[1]
                val coordinates : GeoPoint = GeoPoint(lat, long)
                val imageUris: List<String> = charityRepository.getFridgeImages()
                val randomImageUri = imageUris.random()
                when (val addResult = if (initialFridgeName == null) charityRepository.createCharity(fridgeName, fridgeLocation, coordinates, randomImageUri, fridgeHandle) else null) {
                    is ResponseModel.FAResponse.Error -> {
                        snackbarDelegate.showSnackbar(addResult.error ?: "Unknown error")
                    }
                    else -> {}
                }
            }
            submitButtonUiState.value = submitButtonUiState.value.copy(isLoading = false)
            navigateBack()
        }
    }

    fun navigateBack() {
        appNavigator.navigateBack()
    }
}