import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.QuotaModel
import com.example.farmeraid.ui.theme.LightGrayColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.uicomponents.ProgressBarView
import com.example.farmeraid.uicomponents.models.UiComponentModel

@Composable
fun QuotaItem(
    marketWithQuota: MarketModel.MarketWithQuota,
    modifier : Modifier = Modifier,
    onClick : () -> Unit = {},
) {
    val maxNumOfQuotasShown : Int = 4

    Column (
        modifier = modifier
    ) {
        Text(text = marketWithQuota.name, style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium))
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .border(
                    border = BorderStroke(1.dp, LightGrayColour),
                    shape = RoundedCornerShape(16.dp),
                )
                .background(
                    Color.White,
                )
                .clickable { onClick() }
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            marketWithQuota.quota.produceQuotaList.take(maxNumOfQuotasShown).forEach { produceQuota ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = produceQuota.produceName,
                        modifier = Modifier
                            .width(125.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp
                    )
                    val progressFraction : Float = produceQuota.saleAmount.toFloat() / produceQuota.produceGoalAmount
                    ProgressBarView(
                        progressBarUiState = UiComponentModel.ProgressBarUiState(
                            text = "${(progressFraction * 100).toInt()}%",
                            fontSize = 12.sp,
                            progress = progressFraction,
                            containerColor = PrimaryColour.copy(alpha = 0.2f),
                            progressColor = PrimaryColour,
                        ),
                        modifier = Modifier.weight(1f).fillMaxSize(),
                    )

                }
            }
            if (marketWithQuota.quota.produceQuotaList.size > maxNumOfQuotasShown) {
                Text(
                    text = "+${marketWithQuota.quota.produceQuotaList.size - maxNumOfQuotasShown} more",
                    fontSize = 12.sp,
                    color = Color.Gray,
                )
            }
        }
    }
}

@Preview
@Composable
fun QuotaItemPreview() {
    QuotaItem(
        marketWithQuota = MarketModel.MarketWithQuota(
            id = "0",
            name = "Test",
            prices = hashMapOf("Apples" to 20.0, "Bananas" to 30.0),
            quota = QuotaModel.Quota(
                id = "0",
                produceQuotaList = listOf(
                    QuotaModel.ProduceQuota(
                    produceName = "Apples",
                    produceGoalAmount = 20,
                    saleAmount = 2
                               ),
                    QuotaModel.ProduceQuota(
                    produceName = "Bananas",
                    produceGoalAmount = 40,
                    saleAmount = 5
                    ),
                )
            )
        )
    )
}