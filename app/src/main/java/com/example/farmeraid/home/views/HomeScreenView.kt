import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
                    contentDescription = "Add Quota",
                ),
                fabUiEvent = UiComponentModel.FabUiEvent(
                    onClick = { viewModel.navigateToAddQuota() }
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
        LazyColumn (
            modifier = Modifier
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp),
        ) {
            if (state.selectedTab == Tab.Quotas) {
                items(state.quotasList) { quota ->
                    QuotaItem(quota = quota)
                }
            } else {
                items(state.inventoryList.toList()) { (produceName, produceAmount) ->
                    Column {
                        Text(text = "Produce Name: $produceName")
                        Text(text = "Produce Amount: $produceAmount")
                    }
                }
            }
        }
    }
}