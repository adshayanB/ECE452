package com.example.farmeraid.charity.views

import android.annotation.SuppressLint
import android.location.Location
import android.location.Location.distanceBetween
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
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.FloatingActionButtonView
import com.example.farmeraid.uicomponents.models.UiComponentModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CharityMapsScreenView() {
    val viewModel = hiltViewModel<CharityViewModel>()
    val state by viewModel.state.collectAsState()
    val uwaterloo = LatLng(43.47290, -80.53953)
    val uwaterlooState = MarkerState(position = uwaterloo)

    val sunview = LatLng(43.47315626315881, -80.5323375029304)
    val sunviewState = MarkerState(position = sunview)

    val icon = LatLng(43.4759646231284, -80.53894807959635)
    val iconState = MarkerState(position = icon)

    val distanceA = FloatArray(1)

    Location.distanceBetween(
        uwaterloo.latitude,
        uwaterloo.longitude,
        sunview.latitude,
        sunview.longitude,
        distanceA
    )

    val distanceB = FloatArray(1)

    Location.distanceBetween(
        uwaterloo.latitude,
        uwaterloo.longitude,
        icon.latitude,
        icon.longitude,
        distanceB
    )


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uwaterloo, 14f)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButtonView(
                fabUiState = UiComponentModel.FabUiState(
                    icon = Icons.Filled.Add,
                    contentDescription = "Add New Fridge",
                ),
                fabUiEvent = UiComponentModel.FabUiEvent(
                    onClick = { viewModel.getCoordinatesFromLocation("CN Tower") }
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
                onMapLongClick = {

                }
            ) {
                Marker(
                    state = uwaterlooState,
                    title = "Marker for E7 Building",
                )

                Marker(
                    state = sunviewState,
                    title = "Marker for 208 Sunview",
                )

                Marker(
                    state = iconState,
                    title = "Marker for ICON",
                )
            }
        }

    }


}