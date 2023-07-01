package com.example.farmeraid.uicomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.farmeraid.ui.theme.BlackColour
import com.example.farmeraid.ui.theme.DisabledPrimaryColour
import com.example.farmeraid.ui.theme.DisabledWhiteContentColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
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

            QuantityPickerView(
                quantityPickerUiState = produceItem.quantityPickerState,
                quantityPickerUiEvent = UiComponentModel.QuantityPickerUiEvent(
                    setQuantity = { count -> produceItem.setQuantity(count) },
                    onIncrement = { produceItem.onIncrement() },
                    onDecrement = { produceItem.onDecrement() }
                ),
            )
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
            showPrice = false,
            quantityPickerState = UiComponentModel.QuantityPickerUiState(
                count = 100
            ),
            setQuantity = {},
            onIncrement = {},
            onDecrement = {},
        )
    )
}