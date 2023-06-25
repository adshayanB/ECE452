package com.example.farmeraid.uicomponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.models.UiComponentModel

@Composable
fun FloatingActionButtonView(
    fabUiState: UiComponentModel.FabUiState,
    fabUiEvent: UiComponentModel.FabUiEvent,
    modifier: Modifier = Modifier,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            modifier = modifier
                .padding(all = 20.dp)
                .align(alignment = Alignment.BottomEnd),
            onClick = fabUiEvent.onClick,
            containerColor = PrimaryColour,
        ) {
            if (fabUiState.icon.compareTo("Add") == 0) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add New Item",
                    tint = WhiteContentColour,
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Mic,
                    contentDescription = "Mic",
                    tint = WhiteContentColour,
                )
            }
        }
    }
}

@Preview
@Composable
fun FloatingActionButtonPreview() {
    FloatingActionButtonView(
        fabUiState = UiComponentModel.FabUiState(
            icon = "Add",
        ),
        fabUiEvent = UiComponentModel.FabUiEvent(
            onClick = {},
        )
    )
}