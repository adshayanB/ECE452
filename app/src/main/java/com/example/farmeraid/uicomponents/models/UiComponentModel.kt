package com.example.farmeraid.uicomponents.models

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.farmeraid.navigation.NavRoute

class UiComponentModel {
    // Button Models
    data class ButtonUiState(
        val text: String,
        val enabled: Boolean = true,
    )
    data class ButtonUiEvent(
        val onClick: () -> Unit = {},
    )

    // NavigationBar Models
    data class BottomNavItem(
        val text: String,
        val unselectedIcon: ImageVector,
        val selectedIcon: ImageVector,
        val navigateToRoute: NavRoute,
    )
}