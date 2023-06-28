import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmeraid.farm.FarmViewModel
import com.example.farmeraid.uicomponents.FloatingActionButtonView
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
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = state.speechResult)
        }

    }
}
