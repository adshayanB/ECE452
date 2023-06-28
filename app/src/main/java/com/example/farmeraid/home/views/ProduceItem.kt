import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.farmeraid.home.model.HomeModel
import com.example.farmeraid.ui.theme.LightGrayColour
import com.example.farmeraid.ui.theme.PrimaryColour

@Composable
fun ProduceItem(
    produceName : String,
    produceAmount : Int,
    modifier : Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                border = BorderStroke(1.dp, LightGrayColour),
                shape = RoundedCornerShape(16.dp),
            )
            .background(
                color = Color.White,
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "$produceAmount",
            color = PrimaryColour,
            fontSize = 30.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = produceName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview
@Composable
fun ProduceItemPreview() {
    ProduceItem(
        produceName = "Bananas",
        produceAmount = 300,
    )
}