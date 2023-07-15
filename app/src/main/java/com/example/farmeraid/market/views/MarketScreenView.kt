import android.annotation.SuppressLint
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmeraid.home.HomeViewModel
import com.example.farmeraid.home.model.HomeModel.Tab
import com.example.farmeraid.market.MarketViewModel
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.ui.theme.LightGrayColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.TertiaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.ButtonView
import com.example.farmeraid.uicomponents.FloatingActionButtonView
import com.example.farmeraid.uicomponents.OutlinedButtonView
import com.example.farmeraid.uicomponents.ProgressBarView
import com.example.farmeraid.uicomponents.models.UiComponentModel
import com.google.common.io.Files.append

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreenView() {
    val viewModel = hiltViewModel<MarketViewModel>()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = Tab.Market.name,
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = PrimaryColour,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(25.0.dp),
                    strokeWidth = 3.0.dp,
                )
            }
            else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(30.dp),
                    contentPadding = PaddingValues(20.dp),
                ) {
                    items(state.marketWithQuotaList) { marketWithQuota ->
                        QuotaItem(
                            marketWithQuota = marketWithQuota,
                            onClick = { viewModel.navigateToSellProduce(marketWithQuota.id) }
                        )
                    }
                }
            }
        }
    }
}