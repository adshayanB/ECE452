import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmeraid.home.add_edit_quota.AddEditQuotaViewModel
import com.example.farmeraid.ui.theme.LightGrayColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.SecondaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.ButtonView
import com.example.farmeraid.uicomponents.FloatingActionButtonView
import com.example.farmeraid.uicomponents.QuantityPickerView
import com.example.farmeraid.uicomponents.models.UiComponentModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddEditQuotaScreenView() {
    val viewModel = hiltViewModel<AddEditQuotaViewModel>()
    val state by viewModel.state.collectAsState()

    val dropdownSearchMinSize = 4

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Add Quota",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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
            modifier = Modifier
                .padding(paddingValues)
                .padding(20.dp),
        ) {
            SearchableExpandedDropDownMenuView(
                modifier = Modifier.fillMaxWidth(),
                listOfItems = state.markets,
                dropdownItem = { market ->
                    Text(
                        text = market.name,
                        fontWeight = FontWeight.Medium,
                    )
                },
                selectedOption = state.selectedMarket,
                placeholder = { Text(text = "Select Market") },
                enableSearch = state.markets.size >= dropdownSearchMinSize,
                color = PrimaryColour,
                onDropDownItemSelected = { market -> viewModel.selectMarket(market) },
            )
            Spacer(modifier = Modifier.height(20.dp))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(state.produceRows, key = { it.id }) { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItemPlacement(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SearchableExpandedDropDownMenuView(
                            modifier = Modifier.weight(1f),
                            listOfItems = state.produce.toList().map{ it.first },
                            selectedOption = row.produce,
                            dropdownItem = { produceName ->
                                Text(produceName)
                            },
                            placeholder = { Text(
                                fontSize = 14.sp,
                                text = "Select Produce"
                            ) },
                            enable = state.selectedMarket != null,
                            enableSearch = state.produce.size >= dropdownSearchMinSize,
                            color = PrimaryColour,
                            onDropDownItemSelected = { produceName -> viewModel.selectProduce(row.id, produceName) },
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            QuantityPickerView(
                                quantityPickerUiState = row.quantityPickerUiState,
                                quantityPickerUiEvent = UiComponentModel.QuantityPickerUiEvent(
                                    onIncrement = { viewModel.selectQuotaAmount(
                                        id = row.id,
                                        newAmount = row.quantityPickerUiState.count + 1)
                                    },
                                    onDecrement = { viewModel.selectQuotaAmount(
                                        id = row.id,
                                        newAmount = row.quantityPickerUiState.count - 1)
                                    },
                                    setQuantity = { amount -> viewModel.selectQuotaAmount(
                                        id = row.id,
                                        newAmount = amount)
                                    }
                                ),
                            )
                            if (state.produceRows.size > 1) {
                                Spacer(modifier = Modifier.width(10.dp))
                                Icon(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { viewModel.removeProduceRow(row.id) },
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Remove Produce Row",
                                    tint = LightGrayColour,
                                )
                            }
                        }
                    }
                }
                item {
                    OutlinedIconButton(
                        onClick = { viewModel.addProduceRow() },
                        colors = IconButtonDefaults.outlinedIconButtonColors(
                            contentColor = PrimaryColour,
                        ),
                        border = BorderStroke(1.dp, PrimaryColour),
                        enabled = state.selectedMarket != null,
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Ad Produce Row in Quota")
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            ButtonView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                buttonUiState = state.submitButtonUiState,
                buttonUiEvent = UiComponentModel.ButtonUiEvent(
                    onClick = { viewModel.submitQuota() }
                ),
            )
        }
    }
}