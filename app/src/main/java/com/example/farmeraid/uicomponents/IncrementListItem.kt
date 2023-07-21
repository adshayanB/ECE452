package com.example.farmeraid.uicomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.farmeraid.ui.theme.BlackColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.uicomponents.models.UiComponentModel

@Composable
fun IncrementListItemView(
    produceItem : UiComponentModel.IncrementListItemUiState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    //.weight(1f)
                    .align(Alignment.CenterVertically),
                text = produceItem.title,
                fontWeight = FontWeight.Bold,
                color = BlackColour,
                fontSize = 16.sp
            )

            QuantityPickerView(
                quantityPickerUiState = produceItem.quantityPickerState,
                quantityPickerUiEvent = UiComponentModel.QuantityPickerUiEvent(
                    setQuantity = { count -> produceItem.setQuantity(count) },
                    onIncrement = { produceItem.onIncrement() },
                    onDecrement = { produceItem.onDecrement() }
                ),
            )

            if (produceItem.showPrice) {
                Text(
                    modifier = Modifier
                        //.weight(1f)
                        .align(Alignment.CenterVertically),
                    text = "$${produceItem.price}",
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray,
                    fontSize = 14.sp,
                )
            }
        }
        if (produceItem.showProgressBar) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                ProgressBarView(
                    progressBarUiState = produceItem.progressBarUiState,
                    modifier = Modifier.weight(1f).fillMaxSize(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IncrementListItemPreview() {
    IncrementListItemView(
        produceItem = UiComponentModel.IncrementListItemUiState(
            title = "Apples",
            price = 4.99,
            showPrice = true,
            quantityPickerState = UiComponentModel.QuantityPickerUiState(
                count = 100
            ),
            setQuantity = {},
            onIncrement = {},
            onDecrement = {},
            progressBarUiState = UiComponentModel.ProgressBarUiState(
                text = "30/100",
                progress = 0.1f,
                expectedProgress = 0.1f,
                fontSize = 14.sp,
                containerColor = PrimaryColour.copy(alpha = 0.2f),
                progressColor = PrimaryColour,
            ),
            showProgressBar = true,
        )
    )
}