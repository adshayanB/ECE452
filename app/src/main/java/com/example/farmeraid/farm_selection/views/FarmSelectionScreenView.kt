package com.example.farmeraid.farm_selection.views

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmeraid.farm_selection.FarmSelectionViewModel
import com.example.farmeraid.ui.theme.LightGrayColour

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmSelectionScreenView(
) {
    val viewModel = hiltViewModel<FarmSelectionViewModel>()

    Scaffold {

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding().fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ){
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        border = BorderStroke(1.dp, LightGrayColour),
                        shape = RoundedCornerShape(16.dp),
                    )
                    .background(
                        Color.White,
                    )
                    .clickable {viewModel.navigateToCreateFarm()}
                    .padding(20.dp)
                    .height(200.dp)
                    .width(320.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start

            ){
                Text(
                    modifier = Modifier
                        .padding(top=20.dp),
                    text = "Create a new Farm Organization",
                    style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Medium)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        border = BorderStroke(1.dp, LightGrayColour),
                        shape = RoundedCornerShape(16.dp),
                    )
                    .background(
                        Color.White,
                    )
                    .clickable {viewModel.navigateToJoinFarm()}
                    .padding(20.dp)
                    .height(200.dp)
                    .width(320.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start

            ){
                Text(
                    modifier = Modifier
                        .padding(top=20.dp),
                    text = "Join an existing Farm Organization",
                    style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Medium)
                )
            }
        }

    }
}