import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmeraid.home.HomeViewModel
import com.example.farmeraid.home.model.HomeModel.Tab
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.uicomponents.ButtonView
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
           ButtonView(
               buttonUiState = state.buttonUiState,
               buttonUiEvent = UiComponentModel.ButtonUiEvent(
                   onClick = { viewModel.changeSelectedTabAndNavigate(if (state.selectedTab == Tab.Quotas) Tab.Inventory else Tab.Quotas) }
               )
           )
       },
    ) {
        Text(state.selectedTab.toString())
    }
}