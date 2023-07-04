import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmeraid.home.HomeViewModel
import com.example.farmeraid.home.view_quota.ViewQuotaViewModel
import com.example.farmeraid.ui.theme.BlackColour
import com.example.farmeraid.ui.theme.LightGrayColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.ProgressBarView
import com.example.farmeraid.uicomponents.models.UiComponentModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewQuotaScreenView() {
    val viewModel = hiltViewModel<ViewQuotaViewModel>()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "${state.quota?.name ?: "Unknown"} Quota",
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
                    IconButton(onClick = { viewModel.navigateToEditQuota(state.quota!!.id) }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit ${state.quota?.name ?: "Unknown"} Quota",
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
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            state.quota?.let {
                items(it.quota.produceQuotaList) { produceQuota ->
                    Column(Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier = Modifier
                                    .width(125.dp),
                                text = produceQuota.produceName,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            // TODO: need to get produce sold amount from market and replace the 1 and 1f
                            ProgressBarView(
                                modifier = Modifier.weight(1f).fillMaxSize(),
                                progressBarUiState = UiComponentModel.ProgressBarUiState(
                                    text = "1/${produceQuota.produceGoalAmount}",
                                    fontSize = 14.sp,
                                    progress = 1f/produceQuota.produceGoalAmount,
                                    containerColor = PrimaryColour.copy(alpha = 0.2f),
                                    progressColor = PrimaryColour,
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Divider(modifier = Modifier.height(1.dp), color = LightGrayColour)
                    }
                }
            }
        }

    }
}