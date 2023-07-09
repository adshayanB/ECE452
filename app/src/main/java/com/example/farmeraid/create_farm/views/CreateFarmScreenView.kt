package com.example.farmeraid.create_farm.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmeraid.create_farm.CreateFarmViewModel
import com.example.farmeraid.sign_in.SignInViewModel
import com.example.farmeraid.ui.theme.BlackColour
import com.example.farmeraid.ui.theme.LightGrayColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.uicomponents.ButtonView
import com.example.farmeraid.uicomponents.models.UiComponentModel

//REsource used for design : https://medium.com/@manojbhadane/android-login-screen-using-jetpack-compose-part-2-a262ad87c6d
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFarmScreenView() {
    val viewModel = hiltViewModel<CreateFarmViewModel>()
    val state by viewModel.state.collectAsState()
    Scaffold {
        Column (
            modifier = Modifier.padding(30.dp, 20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
             Column(
                 modifier = Modifier
                     .weight(1f)
                     .fillMaxSize(),
                 horizontalAlignment = Alignment.CenterHorizontally,
                 verticalArrangement = Arrangement.Center,
             ) {
                 Text(text = "Create New Farm", style = TextStyle(fontSize = 30.sp))

                 Spacer(modifier = Modifier.height(50.dp))

                 TextField(
                     label = { Text(text = "Farm Name") },
                     value = state.farmName,
                     onValueChange = { viewModel.setFarmName(it) },
                     colors = TextFieldDefaults.textFieldColors(
                         cursorColor = PrimaryColour,
                         focusedIndicatorColor = PrimaryColour,
                         focusedLabelColor = PrimaryColour,
                         focusedSupportingTextColor = PrimaryColour,
                     ),
                     modifier = Modifier.fillMaxWidth(),
                 )

                 Spacer(modifier = Modifier.height(50.dp))
                 Row(
                     modifier = Modifier
                         .fillMaxWidth(),
                     horizontalArrangement = Arrangement.SpaceAround
                 ) {
                     Button(
                         onClick = { viewModel.navigateBack() },
                         colors = ButtonDefaults.buttonColors(
                             containerColor = LightGrayColour,
                             contentColor = BlackColour,
                         ),
                         modifier = Modifier
                             .height(50.dp)
                             .width(120.dp)
                     )
                     {
                         Text("Prev")
                     }

                     ButtonView(
                         buttonUiState = state.buttonUiState,
                         buttonUiEvent = UiComponentModel.ButtonUiEvent(
                             onClick = { viewModel.submitFarm() }),
                         modifier = Modifier
                             .height(50.dp)
                             .width(120.dp)
                     )
                 }
             }
        }
    }

}