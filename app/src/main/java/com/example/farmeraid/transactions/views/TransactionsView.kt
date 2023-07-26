import android.annotation.SuppressLint
import android.support.customtabs.ICustomTabsCallback
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.farmeraid.data.model.toMessage
import com.example.farmeraid.transactions.TransactionsViewModel
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.OutlinedButtonView
import com.example.farmeraid.uicomponents.TransactionsFilterChip
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.util.UUID

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsView() {
    val viewModel = hiltViewModel<TransactionsViewModel>()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Transactions", color = WhiteContentColour, fontSize = 25.sp)
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
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = PrimaryColour)
            )
        },
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(20.dp, 10.dp, 20.dp, 0.dp),

        ){
            if (state.filterList.isNotEmpty()) {
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    items(state.filterList) {filterChip ->
                        TransactionsFilterChip(
                            modifier = Modifier.width(75.dp),
                            onItemSelected = {id: UUID, item: String->viewModel.updateSelectedFilterItem(id, item)},
                            filter = filterChip,
                            onSelectionCleared = {id: UUID ->viewModel.clearSelectedFilterItem(id)}
                        )
                    }
                    item {
                        TextButton(onClick = {viewModel.clearAllFilterSelections()}) {
                            Text(
                                text = "Clear",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryColour
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Divider()
            }
            if (!state.isLoading) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(0.dp, 20.dp),
                ){
                    items(state.transactionList) { trans ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { },
                            colors = CardDefaults.cardColors(
                                containerColor = trans.transactionType.colour?:Color.White
                            )
                        ){
                            Column(
                                modifier = Modifier
                                    .padding(20.dp)
                            ){
                                Row(modifier = Modifier
                                    .fillMaxWidth(),
                                    horizontalArrangement = if (viewModel.userIsAdmin()) Arrangement.SpaceBetween else Arrangement.Start,
                                ){
                                    Text(modifier = Modifier.offset(0.dp, (-5).dp),
                                        text = trans.transactionType.stringValue,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        fontSize = 25.sp
                                    )
                                    if (viewModel.userIsAdmin()) {
                                        Icon(Icons.Outlined.Close, contentDescription = "Localized description", modifier = Modifier.clickable { viewModel.showDeleteConfirmation(trans) })
                                    }
                                }

                                Spacer(modifier = Modifier.height(5.dp))

                                Row(modifier = Modifier){
                                    Text(
                                        text = trans.toMessage(), color = Color.Black,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = PrimaryColour,
                        modifier = Modifier
                            .size(48.0.dp)
                    )
                }
            }

        }

    }
}

@Preview
@Composable
fun TransactionsPreview(){
    TransactionsView()
}