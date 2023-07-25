package com.example.farmeraid.charity

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.charity.model.CharityModel
import com.example.farmeraid.location_provider.LocationProvider
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.snackbar.SnackbarDelegate
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharityViewModel @Inject constructor(
    private val locationProvider : LocationProvider,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate
) : ViewModel() {
    private val _state = MutableStateFlow(CharityModel.CharityViewState(
        latitude = 0.0,
        longitude = 0.0,
    ))

    val state: StateFlow<CharityModel.CharityViewState>
        get() = _state

    private val longitude : MutableStateFlow<Double> = MutableStateFlow(_state.value.longitude)
    private val latitude : MutableStateFlow<Double> = MutableStateFlow(_state.value.latitude)
    private val readableLocation: MutableStateFlow<String> = MutableStateFlow("")

    //private val listOfFridges : MutableStateFlow<String> = MutableStateFlow(_state.value.listOfFridges)

    init {
        viewModelScope.launch {
            combine(longitude, latitude, readableLocation) {
                    longitude: Double,
                    latitude: Double,
                    readableLocation : String ->
                CharityModel.CharityViewState(
                    longitude = longitude,
                    latitude = latitude,
                    readableLocation = readableLocation,
                )
            }.collect {
                _state.value = it
            }
        }
    }

//    fun getLocationLiveData() = locationProvider

    @Composable
    fun startLocationUpdates() {
        locationProvider.getUserLocation()
    }

    fun stopLocationUpdates() {
        locationProvider.stopLocationUpdate()
    }

    fun getReadableLocation(Lat: Double, Long: Double) {
        //readableLocation.value = locationProvider.getReadableLocation(Lat, Long)

        snackbarDelegate.showSnackbar(
            message = "readable address: ${locationProvider.getReadableLocation(Lat, Long)}"
        )
    }

    fun getCoordinatesFromLocation(locationName : String) {
        //readableLocation.value = locationProvider.getReadableLocation(Lat, Long)

        val coordinates = locationProvider.getCoordinatesFromLocationName(locationName)
        snackbarDelegate.showSnackbar(
            message = "Coordinates: ${coordinates[0]} ${coordinates[1]}"
        )
    }

    fun navigateBack() {
        appNavigator.navigateBack()
    }

    fun navigateToAddFridge() {
        snackbarDelegate.showSnackbar(
            message = "Navigate to Add Fridge Page"
        )
    }

    fun showDistance(distanceA : Float, distanceB: Float) {
        snackbarDelegate.showSnackbar(
            message = "Distance A : ${distanceA}, Distance B: ${distanceB}"
        )
    }

    fun navigateToTransactions() {
        appNavigator.navigateToTransactions()
    }
}