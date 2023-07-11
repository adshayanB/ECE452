package com.example.farmeraid.uicomponents

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.models.UiComponentModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsFilterChip() {
   var isExpanded by remember {
       mutableStateOf(false)
   }

    var transactionType by remember {
        mutableStateOf("")
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {
        TextField(
            value = transactionType,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = "Harvest")
                },
                onClick = {
                    transactionType = "Harvest"
                    isExpanded = false
                }
            )
            DropdownMenuItem(
                text = {
                    Text(text = "Sell")
                },
                onClick = {
                    transactionType = "Sell"
                    isExpanded = false
                }
            )
            DropdownMenuItem(
                text = {
                    Text(text = "Donate")
                },
                onClick = {
                    transactionType = "Donate"
                    isExpanded = false
                }
            )
        }
    }
}

@Preview
@Composable
fun TransactionsFilterChipPreview() {
    TransactionsFilterChip()
}