package com.example.farmeraid.charity

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.charity.model.CharityModel
import com.example.farmeraid.data.CharityRepository
import com.example.farmeraid.data.FarmRepository
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
    private val fridgeList : MutableStateFlow<List<CharityModel.FridgeDetails>> = MutableStateFlow(_state.value.fridgeList)

    init {
        viewModelScope.launch {
            combine(longitude, latitude, userLocation, fridgeList, readableLocation) {
                    longitude: Double,
                    latitude: Double,
                    userLocation: LocationProvider.LatandLong,
                    fridgeList : List<CharityModel.FridgeDetails>,
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
                        CharityModel.FridgeDetails(
                            fridgeProperties = fridge
                        )
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
    fun updateUserLocation(location : LocationProvider.LatandLong) {
        userLocation.value = location

        //Recalculate the distances for each fridge when user location changes
        getFridgeDistances()
    }

    fun getFridgeDistances() : List<CharityModel.FridgeDetails> {
        //calculate the distances for each fridge
        fridgeList.value = fridgeList.value.map{fridge ->
            CharityModel.FridgeDetails(
                fridge.fridgeProperties,
                "%.3f".format(getDistanceFromUser(fridge.fridgeProperties.coordinates)).toFloat()
            )
        }

        //sort the array based on the distance
        //fridgeList.value.sortedBy { it.distanceFromUser }

        fridgeList.value.sortedWith(Comparator { first: CharityModel.FridgeDetails, second: CharityModel.FridgeDetails ->
            if (first.distanceFromUser != second.distanceFromUser) {
                first.distanceFromUser.toInt() - second.distanceFromUser.toInt()
            }
            else {
                first.distanceFromUser.compareTo(second.distanceFromUser)
            }
        })

        return fridgeList.value
    }

    fun getDistanceFromUser(coordinates : LocationProvider.LatandLong) : Float {
        val fridgeLat = coordinates.latitude
        val fridgeLong = coordinates.longitude
        val results = FloatArray(1)

        // Start: User Location  ---> To ---> Dest: Fridge
        Location.distanceBetween(userLocation.value.latitude, userLocation.value.longitude, fridgeLat, fridgeLong, results)

        //convert to km
        return (results[0] / 1000)
    }

    fun stopLocationUpdates() {
        locationProvider.stopLocationUpdate()
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

    fun navigateToFridgeDetails(fridge : CharityModel.FridgeDetails) {
        snackbarDelegate.showSnackbar(
            message = "Navigate to ${fridge.fridgeProperties.fridgeName}"
        )
    }

    fun navigateToTransactions() {
        appNavigator.navigateToTransactions(TransactionModel.TransactionType.DONATE.stringValue)
    }
}