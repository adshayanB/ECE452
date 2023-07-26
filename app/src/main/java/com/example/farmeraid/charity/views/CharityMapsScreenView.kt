package com.example.farmeraid.charity.views

import android.annotation.SuppressLint
import android.location.Location
import android.location.Location.distanceBetween
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmeraid.charity.CharityViewModel
import com.example.farmeraid.location_provider.LocationProvider
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.FloatingActionButtonView
import com.example.farmeraid.uicomponents.models.UiComponentModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CharityMapsScreenView() {
    val viewModel = hiltViewModel<CharityViewModel>()
    val state by viewModel.state.collectAsState()
    var userLocation : LocationProvider.LatandLong = viewModel.locationProvider.getUserLocation()
    var userLocationState = MarkerState(position = LatLng(userLocation.latitude, userLocation.longitude))
    val startLocation = LatLng(43.47310961858659, -80.53947418586205)
    viewModel.setUserLocation(location = userLocation)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startLocation, 15f)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButtonView(
                fabUiState = UiComponentModel.FabUiState(
                    icon = Icons.Filled.Add,
                    contentDescription = "Add New Fridge",
                ),
                fabUiEvent = UiComponentModel.FabUiEvent(
                    onClick = { viewModel.navigateToAddFridge() }
                )
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Charity",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.navigateToTransactions() }) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "Navigate to Transactions Page"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = PrimaryColour,
                    titleContentColor = WhiteContentColour,
                    actionIconContentColor = WhiteContentColour,
                )
            )
        },
    ){innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding),
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false),
            ) {
                Marker(
                    state = userLocationState,
                    title = "My Location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                )

                state.fridgeList.map { fridge ->
                    Marker(
                        state = MarkerState(position = LatLng(fridge.coordinates.latitude, fridge.coordinates.longitude)),
                        title = fridge.fridgeName,
                        snippet = fridge.location,
                    )
                }
            }
        }

    }


}