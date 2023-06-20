import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.farmeraid.home.HomeViewModel
import com.example.farmeraid.home.model.HomeModel.Tab
import com.example.farmeraid.uicomponents.ButtonView
import com.example.farmeraid.uicomponents.OutlinedButtonView
import com.example.farmeraid.uicomponents.models.UiComponentModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView() {
    val viewModel = viewModel(HomeViewModel::class.java)
    val state by viewModel.state.collectAsState()

    Scaffold(
       floatingActionButton = {
           OutlinedButtonView(
               buttonUiState = state.buttonUiState,
               buttonUiEvent = UiComponentModel.ButtonUiEvent(
                   onClick = { viewModel.changeSelectedTab(if (state.selectedTab == Tab.Inventory) Tab.Quotas else Tab.Inventory) }
               )
           )
       },
    ) {
        Text(state.selectedTab.toString())
    }
}