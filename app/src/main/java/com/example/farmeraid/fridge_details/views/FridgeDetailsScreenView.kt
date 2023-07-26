package com.example.farmeraid.fridge_details.views

import android.annotation.SuppressLint
import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ChipColors
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.farmeraid.fridge_details.FridgeDetailsViewModel
import com.example.farmeraid.ui.theme.LightGrayColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.SecondaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.ButtonView
import com.example.farmeraid.uicomponents.IncrementListItemView
import com.example.farmeraid.uicomponents.models.UiComponentModel
import com.google.android.material.chip.Chip

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridgeDetailsView(){
    val viewModel = hiltViewModel<FridgeDetailsViewModel>()
    val state by viewModel.state.collectAsState()

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        state.fridgeDetails?.fridgeName.toString(),
                        maxLines = 1,   
                        overflow = TextOverflow.Ellipsis
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }){
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                            tint = WhiteContentColour,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {viewModel.navigateToFridge()}) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = WhiteContentColour,
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = PrimaryColour,
                    titleContentColor = WhiteContentColour,
                    actionIconContentColor = WhiteContentColour,
                )
            )
        }
            ) { padding ->
            if(state.fridgeDetails != null){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)

                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(20.dp, 0.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,

                                ) {
                                Image(
                                    painter = rememberAsyncImagePainter(state.fridgeDetails!!.imageUri),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .padding(0.dp, 20.dp,0.dp, 0.dp)
                                        .clip(RoundedCornerShape(15.dp)),
                                    alignment = Alignment.Center,
                                    contentScale = ContentScale.Crop,


                                )
                            }
                        }
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                //ROW FOR PILLS
                                Column(
                                    modifier = Modifier
                                ) {
                                    Text(
                                        "Fridge Items",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        items(state.fridgeInventory) { detected ->
                                            SuggestionChip(
                                                label = {
                                                    Text(
                                                        detected,
                                                        fontWeight = FontWeight.W400
                                                    )
                                                },
                                                onClick = { null },
                                                colors = SuggestionChipDefaults.suggestionChipColors(
                                                    containerColor = Color(0xFFE5F0C8),
                                                    labelColor = Color.Black,
                                                ),
                                                border = null,
                                                shape = RoundedCornerShape(25.dp),

                                                )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Divider(modifier = Modifier.height(1.dp), color = LightGrayColour)
                                }
                            }
                        }

                        items(state.farmProduces) { produce ->
                            IncrementListItemView(
                                produceItem = UiComponentModel.IncrementListItemUiState(
                                    title = produce.produceName,
                                    quantityPickerState = UiComponentModel.QuantityPickerUiState(produce.produceCount),
                                    onIncrement = { viewModel.incrementProduceCount(produce.produceName) },
                                    onDecrement = { viewModel.decrementProduceCount(produce.produceName) },
                                    setQuantity = { count ->
                                        viewModel.setProduceCount(
                                            produce.produceName,
                                            count
                                        )
                                    },
                                ),
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Divider(modifier = Modifier.height(1.dp), color = LightGrayColour)
                        }
                    }
                    ButtonView(
                        buttonUiState = state.donateButtonUiState,
                        buttonUiEvent = UiComponentModel.ButtonUiEvent(
                            onClick = { viewModel.donateProduce() }),
                        modifier = Modifier
                            .padding(20.dp, 0.dp, 20.dp, 20.dp)
                            .fillMaxWidth()
                            .height(50.dp)
                    )
                }
            }
    }

}