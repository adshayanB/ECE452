package com.example.farmeraid.uicomponents

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.farmeraid.transactions.model.TransactionsModel
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.ui.theme.WhiteContentColour
import com.example.farmeraid.uicomponents.models.UiComponentModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsFilterChip(
    filter: TransactionsModel.Filter,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(filter.name) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            // expanded = !expanded
        }
    ) {
        InputChip(
            label = {
                Text(
                    modifier = modifier,
                    text=selectedText,
                    maxLines=1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            selected = expanded,
            onClick = { expanded = !expanded },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filter.itemsList.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        selectedText = item
                        expanded = false
                        Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

//@ExperimentalMaterial3Api
//@Composable
//fun TransactionsInputChip( selected: Boolean, selectedText: String ){
//    InputChip(
//        selected = selected,
//        onClick = { selected = !selected },
//        label = {Text(selectedText)},
//        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = selected) },
//        modifier = Modifier.padding(horizontal = 4.dp)
//    )
//
//}

//   var isExpanded by remember {
//       mutableStateOf(false)
//   }
//
//    var transactionType by remember {
//        mutableStateOf("")
//    }
//
//    ExposedDropdownMenuBox(
//        expanded = isExpanded,
//        onExpandedChange = { isExpanded = it }
//    ) {
//        TextField(
//            value = transactionType,
//            onValueChange = {},
//            readOnly = true,
//            trailingIcon = {
//                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
//            },
//            colors = ExposedDropdownMenuDefaults.textFieldColors(),
//            modifier = Modifier.menuAnchor()
//        )
//
//        ExposedDropdownMenu(
//            expanded = isExpanded,
//            onDismissRequest = { isExpanded = false }
//        ) {
//            DropdownMenuItem(
//                text = {
//                    Text(text = "Harvest")
//                },
//                onClick = {
//                    transactionType = "Harvest"
//                    isExpanded = false
//                }
//            )
//            DropdownMenuItem(
//                text = {
//                    Text(text = "Sell")
//                },
//                onClick = {
//                    transactionType = "Sell"
//                    isExpanded = false
//                }
//            )
//            DropdownMenuItem(
//                text = {
//                    Text(text = "Donate")
//                },
//                onClick = {
//                    transactionType = "Donate"
//                    isExpanded = false
//                }
//            )
//        }
//    }
// }

@Preview
@Composable
fun TransactionsFilterChipPreview() {

}