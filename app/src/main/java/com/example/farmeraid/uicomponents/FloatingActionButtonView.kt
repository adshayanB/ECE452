package com.example.farmeraid.uicomponents

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.models.UiComponentModel

@Composable
fun FloatingActionButtonView(
    fabUiState: UiComponentModel.FabUiState,
    fabUiEvent: UiComponentModel.FabUiEvent,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = fabUiEvent.onClick,
        containerColor = PrimaryColour,
        shape = CircleShape,
    ) {
        Icon(
            imageVector = fabUiState.icon,
            contentDescription = fabUiState.contentDescription,
            tint = WhiteContentColour,
        )
    }
}

@Preview
@Composable
fun FloatingActionButtonPreview() {
    FloatingActionButtonView(
        fabUiState = UiComponentModel.FabUiState (
            icon = Icons.Filled.Add,
        ),
        fabUiEvent = UiComponentModel.FabUiEvent(
            onClick = {},
        )
    )
}