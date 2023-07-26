import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmeraid.farm.FarmViewModel
import com.example.farmeraid.ui.theme.LightGrayColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.ButtonView
import com.example.farmeraid.uicomponents.FloatingActionButtonView
import com.example.farmeraid.uicomponents.IncrementListItemView
import com.example.farmeraid.uicomponents.models.UiComponentModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmScreenView() {
    val viewModel = hiltViewModel<FarmViewModel>()
    val state by viewModel.state.collectAsState()

    Scaffold (
        floatingActionButton = {
            FloatingActionButtonView(
                fabUiState = state.micFabUiState,
                fabUiEvent = state.micFabUiEvent
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Harvest",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.navigateToTransactions() }) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "Localized description"
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
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp, 0.dp, 20.dp, 20.dp),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = PrimaryColour,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(25.0.dp),
                    strokeWidth = 3.0.dp,
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp, 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    items(state.produceHarvestList) { produce ->
                        IncrementListItemView(
                            produceItem = UiComponentModel.IncrementListItemUiState(
                                title = produce.produceName,
                                quantityPickerState = UiComponentModel.QuantityPickerUiState(
                                    produce.produceCount ?: 0
                                ),
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
                    buttonUiState = state.submitButtonUiState,
                    buttonUiEvent = UiComponentModel.ButtonUiEvent(
                        onClick = { viewModel.submitHarvest() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )
            }
        }

    }
}
