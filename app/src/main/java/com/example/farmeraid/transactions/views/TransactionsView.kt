import android.annotation.SuppressLint
import android.support.customtabs.ICustomTabsCallback
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.navigation.compose.rememberNavController
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsView() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Farm Mode", color = WhiteContentColour, fontSize = 25.sp)
                },
                navigationIcon = {
                    IconButton(onClick = {}){
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
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
            items(5) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { },
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    )
                ){
                    Row(modifier = Modifier
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(modifier = Modifier
                            .padding(10.dp),
                            text = "Test", color = Color.Black,
                            fontSize = 25.sp
                        )
                        IconButton(modifier = Modifier,
                            onClick = { /* doSomething() */ }) {
                            Icon(Icons.Outlined.Close, contentDescription = "Localized description")
                        }
                    }
                    Row(

                    ){
                        Text(modifier = Modifier
                            .padding(10.dp, 0.dp, 0.dp, 10.dp),
                            text = "Test", color = Color.Black,
                            fontSize = 18.sp
                        )
                    }

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