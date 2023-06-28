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

    // Increment List Item Models
    data class IncrementListItemUiState(
        val id: Int,
        val title: String,
        val price: Double,
        val quantity : Int,
        val showPrice: Boolean = false,
        val quantityPickerState : QuantityPickerUiState,
        val onIncrement: () -> Unit,
        val onDecrement: () -> Unit,
        val setQuantity: () -> Unit,
    )

    // Quantity Picker Models
    data class QuantityPickerUiState(
        var count: Int = 0,
    )

    data class QuantityPickerUiEvent(
        val setQuantity: (Int) -> Unit = {},
        val onIncrement: () -> Unit = {},
        val onDecrement: () -> Unit = {},
    )
}