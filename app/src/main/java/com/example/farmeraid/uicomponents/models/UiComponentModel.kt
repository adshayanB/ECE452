package com.example.farmeraid.uicomponents.models

import android.graphics.drawable.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
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

    // Progress Bar Models
    data class ProgressBarUiState(
        val text : String,
        val containerColor : Color = Color.White,
        val progressColor : Color = Color.Black,
        val progress : Float = 0f,
        val fontSize : TextUnit = 14.sp,
        val fontWeight : FontWeight = FontWeight.Normal,
    )
}