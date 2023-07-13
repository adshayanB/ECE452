package com.example.farmeraid.uicomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.farmeraid.ui.theme.DisabledPrimaryColour
import com.example.farmeraid.ui.theme.DisabledWhiteContentColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.SecondaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.models.UiComponentModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuantityPickerView(
    quantityPickerUiState: UiComponentModel.QuantityPickerUiState,
    quantityPickerUiEvent: UiComponentModel.QuantityPickerUiEvent,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
    ) {
        FilledIconButton( modifier = Modifier
            .align(Alignment.CenterVertically)
            .padding(end = 5.dp)
            .size(size = 40.dp),
            onClick = quantityPickerUiEvent.onDecrement,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = SecondaryColour,
                contentColor = WhiteContentColour,
            ),
            enabled = quantityPickerUiState.count > 0 && quantityPickerUiState.enabled
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "Decrement",
            )
        }

        CustomTextField(value = "${quantityPickerUiState.count}",
            modifier = Modifier
                .width(55.dp)
                .align(Alignment.CenterVertically),
            onValueChange = { quantityPickerUiEvent.setQuantity(it.toInt()) },
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

//        TextField(
//            value = "${quantityPickerUiState.count}",
//            onValueChange = { quantityPickerUiEvent.setQuantity(it.toInt()) },
//            modifier = Modifier
//                .width(55.dp)
//                .height(48.dp),
//            colors = TextFieldDefaults.textFieldColors(
//                cursorColor = PrimaryColour,
//                focusedIndicatorColor = PrimaryColour,
//                focusedLabelColor = PrimaryColour,
//                focusedSupportingTextColor = PrimaryColour,
//            ),
//            textStyle = LocalTextStyle.current.copy(
//                fontSize = 14.sp,
//                textAlign = TextAlign.Center,
//            ),
//            enabled = quantityPickerUiState.enabled,
//            // TODO: Fix textfield so that we can write in it
//            readOnly = true,
//        )
        FilledIconButton(modifier = Modifier
            .align(Alignment.CenterVertically)
            .padding(start = 5.dp)
            .size(size = 40.dp),
            onClick = quantityPickerUiEvent.onIncrement,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = SecondaryColour,
                contentColor = WhiteContentColour,
            ),
            enabled = quantityPickerUiState.enabled,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Increment",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuantityPickerPreview() {
    QuantityPickerView(
        quantityPickerUiState = UiComponentModel.QuantityPickerUiState(
            count = 999
        ),
        quantityPickerUiEvent = UiComponentModel.QuantityPickerUiEvent(
            setQuantity = {},
            onIncrement = {},
            onDecrement = {}
        ),
    )
}