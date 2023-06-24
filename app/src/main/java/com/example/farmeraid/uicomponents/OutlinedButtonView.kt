package com.example.farmeraid.uicomponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.farmeraid.ui.theme.DisabledPrimaryColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.uicomponents.models.UiComponentModel

@Composable
fun OutlinedButtonView(
    buttonUiState: UiComponentModel.ButtonUiState,
    buttonUiEvent: UiComponentModel.ButtonUiEvent,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        modifier = modifier,
        onClick = buttonUiEvent.onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = PrimaryColour,
            disabledContentColor = DisabledPrimaryColour,
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = PrimaryColour,
        ),
        enabled = !buttonUiState.isLoading && buttonUiState.enabled,
    ) {
        if (buttonUiState.isLoading) {
            CircularProgressIndicator(
                color = PrimaryColour,
                modifier = Modifier.padding(2.0.dp)
            )
        } else {
            Text(buttonUiState.text)
        }
    }
}

@Preview
@Composable
fun OutlinedButtonPreview() {
    OutlinedButtonView(
        buttonUiState = UiComponentModel.ButtonUiState(
            text = "Cancel",
            enabled = true,
        ),
        buttonUiEvent = UiComponentModel.ButtonUiEvent(
            onClick = {},
        )
    )
}