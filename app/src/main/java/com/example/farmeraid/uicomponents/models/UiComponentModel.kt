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
        val title: String,
        val onIncrement: () -> Unit,
        val onDecrement: () -> Unit,
        val setQuantity: (count : Int) -> Unit,
        val quantityPickerState : QuantityPickerUiState,
        val price: Double? = null,
        val showPrice: Boolean = false,
    )

    // Increment List Dropdown Item Models
    data class IncrementListDropdownItemUiState(
        val title: String,
        val onIncrement: () -> Unit,
        val onDecrement: () -> Unit,
        val setQuantity: (count : Int) -> Unit,
        val quantityPickerState : QuantityPickerUiState,
        val price: Double? = null,
        val showPrice: Boolean = false,
    )

    // Quantity Picker Models
    data class QuantityPickerUiState(
        val count : Int = 0,
        val enabled : Boolean = true,
    )

    data class QuantityPickerUiEvent(
        val setQuantity: (Int) -> Unit = {},
        val onIncrement: () -> Unit = {},
        val onDecrement: () -> Unit = {},
    )
}