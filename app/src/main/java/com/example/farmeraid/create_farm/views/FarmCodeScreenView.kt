package com.example.farmeraid.create_farm.views

import android.annotation.SuppressLint
import android.media.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmeraid.create_farm.FarmCodeViewModel
import com.example.farmeraid.ui.theme.BlackColour
import com.example.farmeraid.ui.theme.LightGrayColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.uicomponents.ButtonView
import com.example.farmeraid.uicomponents.models.UiComponentModel

//REsource used for design : https://medium.com/@manojbhadane/android-login-screen-using-jetpack-compose-part-2-a262ad87c6d
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmCodeScreenView() {
    val viewModel = hiltViewModel<FarmCodeViewModel>()
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

                 //TODO: Display Success Image
//                 Image(
//                     painter = painterResource(R.drawable.your_drawable),
//                     contentDescription = "Content description for visually impaired"
//                 )

                 Text(text = "Success!", style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.SemiBold))

                 Spacer(modifier = Modifier.height(20.dp))

                 Text(text = "A new farm has been created!", style = TextStyle(fontSize = 18.sp))

                 Spacer(modifier = Modifier.height(10.dp))

                 // TODO: need to get the RNG Farm Code
                 Text(text = "Farm Code: 12345", style = TextStyle(fontSize = 18.sp))

                 Spacer(modifier = Modifier.height(20.dp))

                 ButtonView(
                     buttonUiState = state.buttonUiState,
                     buttonUiEvent = UiComponentModel.ButtonUiEvent(
                         onClick = { viewModel.navigateToHome() }),
                     modifier = Modifier
                         .fillMaxWidth()
                         .height(50.dp)
                 )
             }
        }
    }

}