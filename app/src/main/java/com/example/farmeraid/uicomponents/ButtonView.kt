package com.example.farmeraid.uicomponents

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.farmeraid.ui.theme.DisabledPrimaryColour
import com.example.farmeraid.ui.theme.DisabledWhiteContentColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.models.UiComponentModel

@Composable
fun ButtonView(
    buttonUiState: UiComponentModel.ButtonUiState,
    buttonUiEvent: UiComponentModel.ButtonUiEvent,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier,
        onClick = buttonUiEvent.onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryColour,
            contentColor = WhiteContentColour,
            disabledContainerColor = DisabledPrimaryColour,
            disabledContentColor = DisabledWhiteContentColour,
        ),
        enabled = !buttonUiState.isLoading && buttonUiState.enabled,
    ) {
        if (buttonUiState.isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(25.0.dp),
                strokeWidth = 3.0.dp,
            )
        } else {
            Text(buttonUiState.text)
        }
    }
}

@Preview
@Composable
fun ButtonPreview() {
    ButtonView(
        buttonUiState = UiComponentModel.ButtonUiState(
            text = "Submit",
            enabled = true,
        ),
        buttonUiEvent = UiComponentModel.ButtonUiEvent(
            onClick = {},
        )
    )
}