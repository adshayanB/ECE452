package com.example.farmeraid.uicomponents

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.farmeraid.transactions.TransactionsViewModel
import com.example.farmeraid.transactions.model.TransactionsModel
import com.example.farmeraid.ui.theme.LightGrayColour
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.models.UiComponentModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsFilterChip(
    filter: TransactionsModel.Filter,
    onItemSelected: (id: UUID, selectedItem: String) -> Unit,
    onSelectionCleared: (id: UUID) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
        }
    ) {
        InputChip(
            label = {
                Text(
                    modifier = modifier,
                    text = filter.selectedItem ?: filter.name.uiName, // if left is null, do right, else do left
                    maxLines=1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            selected = expanded,
            onClick = { expanded = !expanded },
            trailingIcon = {
                if (filter.selectedItem != null){
                    Icon(
                        modifier = Modifier.clickable { onSelectionCleared(filter.id) },
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear Selection",
                        tint = Color.Black
                    )
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
                           },
            modifier = Modifier.menuAnchor(),
            colors = InputChipDefaults.inputChipColors(
                containerColor = filter.selectedItem?.let { PrimaryColour.copy(alpha = 0.1f) } ?: Color.White
            ),
            border = InputChipDefaults.inputChipBorder(
                borderColor = filter.selectedItem?.let { PrimaryColour }?: Color.Black
            )
        )
        if (expanded){
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                filter.itemsList.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            expanded = false
                            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                            onItemSelected(filter.id, item)
                        }
                    )
                }
            }
        }
    }
}