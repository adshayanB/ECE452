import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmeraid.home.HomeViewModel
import com.example.farmeraid.home.model.HomeModel.Tab
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.uicomponents.ButtonView
import com.example.farmeraid.uicomponents.FloatingActionButtonView
import com.example.farmeraid.uicomponents.OutlinedButtonView
import com.example.farmeraid.uicomponents.models.UiComponentModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView() {
    val viewModel = hiltViewModel<HomeViewModel>()
    val state by viewModel.state.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButtonView(
                fabUiState = UiComponentModel.FabUiState(
                    icon = Icons.Filled.Add,
                    contentDescription = if (state.selectedTab == Tab.Quotas)  "Add Quota" else "Add Produce",
                ),
                fabUiEvent = UiComponentModel.FabUiEvent(
                    onClick = {
                        if (state.selectedTab == Tab.Quotas) {
                            viewModel.navigateToAddQuota()
                        } else {
                            viewModel.navigateToAddProduce()
                        }
                    }
                )
            )
        },
        topBar = {
            TabRow(
                selectedTabIndex = state.selectedTab.index,
                contentColor = PrimaryColour,
                indicator = { tabPositions -> TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTab.index]),
                    color = PrimaryColour,
                ) },
            ) {
                Tab(
                    selected = state.selectedTab == Tab.Quotas,
                    onClick = { viewModel.changeSelectedTab(Tab.Quotas) },
                    text = {
                        Text(
                            text = Tab.Quotas.name,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
                Tab(
                    selected = state.selectedTab == Tab.Inventory,
                    onClick = { viewModel.changeSelectedTab(Tab.Inventory) },
                    text = {
                        Text(
                            text = Tab.Inventory.name,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
    ) { paddingValues ->
        if (state.selectedTab == Tab.Quotas) {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(30.dp),
            ) {
                items(state.quotasList) { quota ->
                    QuotaItem(quota = quota)
                }
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(20.dp),
                columns = GridCells.Adaptive(150.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                items(state.inventoryList.toList()) { (produceName, produceAmount) ->
                    ProduceItem(modifier = Modifier.fillMaxHeight(), produceName = produceName, produceAmount = produceAmount)
                }
            }
        }
    }
}