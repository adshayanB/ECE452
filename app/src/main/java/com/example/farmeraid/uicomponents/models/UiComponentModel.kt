package com.example.farmeraid.uicomponents.models

import android.graphics.drawable.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.farmeraid.navigation.NavRoute

class UiComponentModel {
    // Button Models
    data class ButtonUiState(
        val text: String,
        val isLoading: Boolean = false,
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

    // Floating Action Button Models
    data class FabUiState(
        val icon: ImageVector,
        val contentDescription: String? = null,
    )

    data class FabUiEvent(
        val onClick: () -> Unit = {},
    )
}