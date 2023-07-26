package com.example.farmeraid.charity

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.charity.model.CharityModel
import com.example.farmeraid.data.CharityRepository
import com.example.farmeraid.data.FarmRepository
import com.example.farmeraid.data.model.FridgeModel
import com.example.farmeraid.data.model.TransactionModel
import com.example.farmeraid.location_provider.LocationProvider
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.snackbar.SnackbarDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharityViewModel @Inject constructor(
    private val farmRepository: FarmRepository,
    private val charityRepository: CharityRepository,
    val locationProvider : LocationProvider,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate
) : ViewModel() {
    private val _state = MutableStateFlow(CharityModel.CharityViewState(
        latitude = 0.0,
        longitude = 0.0,
        userLocation = LocationProvider.LatandLong()
    ))

    val state: StateFlow<CharityModel.CharityViewState>
        get() = _state

    private val longitude : MutableStateFlow<Double> = MutableStateFlow(_state.value.longitude)
    private val latitude : MutableStateFlow<Double> = MutableStateFlow(_state.value.latitude)
    private val readableLocation: MutableStateFlow<String> = MutableStateFlow("")
    private val userLocation : MutableStateFlow<LocationProvider.LatandLong> = MutableStateFlow(_state.value.userLocation)
    private val fridgeList : MutableStateFlow<List<FridgeModel.Fridge>> = MutableStateFlow(_state.value.fridgeList)

    init {
        viewModelScope.launch {
            combine(longitude, latitude, userLocation, fridgeList, readableLocation) {
                    longitude: Double,
                    latitude: Double,
                    userLocation: LocationProvider.LatandLong,
                    fridgeList : List<FridgeModel.Fridge>,
                    readableLocation : String, ->
                CharityModel.CharityViewState(
                    longitude = longitude,
                    latitude = latitude,
                    readableLocation = readableLocation,
                    fridgeList = fridgeList,
                    userLocation = userLocation,
                )
            }.collect {
                _state.value = it
            }
        }
    }

    init {
        viewModelScope.launch{
            fridgeList.value = farmRepository.getCharityIds().let { charities ->
                charities.data?.mapNotNull { id ->
                    charityRepository.getCharity(id).data?.let{ fridge ->
                        fridge
                    }
                } ?: run {
                    snackbarDelegate.showSnackbar(charities.error ?: "Unknown error")
                    fridgeList.value
                }
            }
        }
    }

    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun setUserLocation(location : LocationProvider.LatandLong) {
        userLocation.value = location
    }

    fun stopLocationUpdates() {
        locationProvider.stopLocationUpdate()
    }

    fun displayCoordinates(Lat: Double, Long: Double) {

        snackbarDelegate.showSnackbar(
            message = "Coordinates: ${Lat} ${Long}"
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getCoordinatesFromLocation(locationName : String) {
        val coordinates = locationProvider.getCoordinatesFromLocationName(locationName)
        userLocation.value.latitude = coordinates[0]
        userLocation.value.longitude = coordinates[1]

        snackbarDelegate.showSnackbar(
            message = "Coordinates: ${userLocation.value.latitude} ${userLocation.value.longitude}"
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
        appNavigator.navigateToTransactions(TransactionModel.TransactionType.DONATE.stringValue)
    }
}