package com.example.farmeraid.fridge.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmeraid.charity.CharityViewModel
import com.example.farmeraid.fridge.AddEditFridgeViewModel
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.ButtonView
import com.example.farmeraid.uicomponents.models.UiComponentModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFridgeScreenView() {
    val viewModel = hiltViewModel<AddEditFridgeViewModel>()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (state.isAddFridge) {
                        Text(
                            text = "Add Fridge",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            text = "Edit ${state.fridgeName}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }){
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "",
                            tint = WhiteContentColour,
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = PrimaryColour,
                    titleContentColor = WhiteContentColour,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            TextField(
                label = { Text(text = "Fridge Name") },
                value = state.fridgeName ?: "",
                onValueChange = { viewModel.setFridgeName(it)},
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = PrimaryColour,
                    focusedIndicatorColor = PrimaryColour,
                    focusedLabelColor = PrimaryColour,
                    focusedSupportingTextColor = PrimaryColour,
                ),
                readOnly = !state.isAddFridge,
                modifier = Modifier.fillMaxWidth(),
            )
            TextField(
                label = { Text(text = "Fridge Location") },
                value = state.fridgeLocation ?: "",
                onValueChange = { viewModel.setFridgeLocation(it)},
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = PrimaryColour,
                    focusedIndicatorColor = PrimaryColour,
                    focusedLabelColor = PrimaryColour,
                    focusedSupportingTextColor = PrimaryColour,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            TextField(
                label = { Text(text = "Fridge Instagram Handle") },
                value = state.fridgeHandle ?: "",
                onValueChange = { viewModel.setFridgeHandle(it)},
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = PrimaryColour,
                    focusedIndicatorColor = PrimaryColour,
                    focusedLabelColor = PrimaryColour,
                    focusedSupportingTextColor = PrimaryColour,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            ButtonView(
                buttonUiState = state.submitButtonUiState,
                buttonUiEvent = UiComponentModel.ButtonUiEvent(
                    onClick = { viewModel.submitFridge() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }
    }
}