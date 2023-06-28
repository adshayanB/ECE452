import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.home.model.HomeModel
import com.example.farmeraid.ui.theme.LightGrayColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.uicomponents.OutlinedButtonView
import com.example.farmeraid.uicomponents.models.UiComponentModel

@Composable
fun QuotaItem(
    quota : QuotasRepository.Quota,
    modifier : Modifier = Modifier,
) {
    Column (
        modifier = modifier
            .background(
                Color.White,
            )
    ) {
        Text(text = quota.marketName, style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Medium))
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .border(
                    border = BorderStroke(1.dp, LightGrayColour),
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            quota.produceQuotaList.forEach { produceQuota ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .height(22.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        text = produceQuota.produceName,
                        modifier = Modifier.width(125.dp).offset(0.dp, 4.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 16.sp
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.End,
                    ) {
                        val progressFraction : Float = produceQuota.produceSoldAmount.toFloat() / produceQuota.produceGoalAmount
                        Text(
                            text ="${(progressFraction * 100).toInt()}%",
                            fontSize = 10.sp,
                        )
                        LinearProgressIndicator(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .fillMaxWidth()
                                .weight(1f),
                            progress = progressFraction,
                            color = PrimaryColour,
                            trackColor = PrimaryColour.copy(alpha = 0.2f)
                        )
                    }

                }
            }
        }
    }
}

@Preview
@Composable
fun QuotaItemPreview() {
    QuotaItem(
        quota = QuotasRepository.Quota(
            marketName = "Test",
            produceQuotaList = listOf(
                QuotasRepository.ProduceQuota(
                    produceName = "Apples",
                    produceSoldAmount = 10,
                    produceGoalAmount = 20,
                ),
                QuotasRepository.ProduceQuota(
                    produceName = "Bananas",
                    produceSoldAmount = 10,
                    produceGoalAmount = 40,
                ),
            )
        )
    )
}