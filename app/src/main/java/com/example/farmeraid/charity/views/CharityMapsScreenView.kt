package com.example.farmeraid.charity.views

import android.annotation.SuppressLint
import android.location.Location
import android.location.Location.distanceBetween
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmeraid.charity.CharityViewModel
import com.example.farmeraid.location_provider.LocationProvider
import com.example.farmeraid.ui.theme.LightGrayColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.ButtonView
import com.example.farmeraid.uicomponents.FloatingActionButtonView
import com.example.farmeraid.uicomponents.ProgressBarView
import com.example.farmeraid.uicomponents.models.UiComponentModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
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
    val startLocation = LatLng(43.47310961858659, -80.53947418586205)
    viewModel.updateUserLocation(location = userLocation)

    var userLocationState = MarkerState(position = LatLng(state.userLocation.latitude, state.userLocation.longitude))

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startLocation, 15f)
    }

   BottomSheetScaffold(
       sheetContent = {
           Column(horizontalAlignment = Alignment.CenterHorizontally) {
               LazyColumn(
                   modifier = Modifier
                       .fillMaxWidth(),
                   contentPadding = PaddingValues(20.dp),
                   verticalArrangement = Arrangement.spacedBy(20.dp),
               ) {
                   items(state.fridgeList) { fridge ->
                       Column(
                           Modifier
                               .fillMaxWidth()
                               .clickable {
                                   viewModel.navigateToFridgeDetails(fridge)
                               },
                       ) {
                           Text(
                               modifier = Modifier
                                   .fillMaxWidth(),
                               text = fridge.fridgeProperties.fridgeName,
                               fontWeight = FontWeight.Medium,
                               fontSize = 18.sp,
                               maxLines = 2,
                               overflow = TextOverflow.Ellipsis,
                           )
                           Spacer(modifier = Modifier.height(10.dp))
                           Text(
                               modifier = Modifier
                                   .fillMaxWidth(),
                               text = "${fridge.distanceFromUser} km",
                               fontWeight = FontWeight.Normal,
                               fontSize = 14.sp,
                               maxLines = 2,
                               overflow = TextOverflow.Ellipsis,
                           )
                           Spacer(modifier = Modifier.height(20.dp))
                           Divider(modifier = Modifier.height(1.dp), color = LightGrayColour)
                       }
                   }
               }

               ButtonView(
                   buttonUiState = UiComponentModel.ButtonUiState(
                       text = "Add"
                   ),
                   buttonUiEvent = UiComponentModel.ButtonUiEvent(
                       onClick = { viewModel.navigateToAddFridge() }
                   ),
                   modifier = Modifier
                       .height(50.dp)
                       .width(120.dp)
               )
               Spacer(modifier = Modifier.height(10.dp))
           }
       },
       sheetContainerColor = Color.White,
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
   ) {innerPadding ->
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
                        state = MarkerState(
                            position = LatLng(
                                fridge.fridgeProperties.coordinates.latitude,
                                fridge.fridgeProperties.coordinates.longitude
                            )
                        ),
                        title = fridge.fridgeProperties.fridgeName,
                        snippet = fridge.fridgeProperties.location,
                    )
                }
            }
        }
    }
}