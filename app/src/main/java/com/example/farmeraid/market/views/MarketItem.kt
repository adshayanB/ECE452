package com.example.farmeraid.market.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.ui.theme.LightGrayColour
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MarketItem(
    market: MarketModel.Market,
    modifier : Modifier = Modifier,
    onClick : () -> Unit = {},
) {
    val maxNumOfProduceShown : Int = 4
    val numberFormat = NumberFormat.getCurrencyInstance(Locale.CANADA)

    Column (
        modifier = modifier
    ) {
        Text(text = market.name, style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium))
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
            market.prices.toList().take(maxNumOfProduceShown).forEach { producePrice ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = producePrice.first,
                        modifier = Modifier
                            .width(125.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "${numberFormat.format(producePrice.second)}/produce",
                        modifier = Modifier.width(125.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp
                    )
                }
            }
            if (market.prices.size > maxNumOfProduceShown) {
                Text(
                    text = "+${market.prices.size - maxNumOfProduceShown} more",
                    fontSize = 12.sp,
                    color = Color.Gray,
                )
            }
        }
    }
}