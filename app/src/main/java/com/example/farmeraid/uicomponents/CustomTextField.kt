package com.example.farmeraid.uicomponents

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.farmeraid.ui.theme.PrimaryColour
import com.example.farmeraid.uicomponents.models.UiComponentModel

data class CustomTextFieldColors(
    val textColor: Color = Color.Black,
    val containerColor: Color = Color.White,
    val cursorColor: Color = PrimaryColour,
    val focusedIndicatorColor: Color = PrimaryColour,
    val focusedSupportingTextColor: Color = PrimaryColour,
    val focusedLabelColor: Color = PrimaryColour,
)

@ExperimentalMaterial3Api
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.filledShape,
    colors: CustomTextFieldColors = CustomTextFieldColors()
) {
    // If color is not provided via the text style, use content color as a default
    val textColor = textStyle.color.takeOrElse {
        colors.textColor
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    BasicTextField(
        value = value,
        modifier = modifier,
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(colors.cursorColor),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
//        decorationBox = @Composable { innerTextField ->
//            // places leading icon, text field with label and placeholder, trailing icon
//            TextFieldDefaults.TextFieldDecorationBox(
//                value = value,
//                visualTransformation = visualTransformation,
//                innerTextField = innerTextField,
//                placeholder = placeholder,
//                label = label,
//                leadingIcon = leadingIcon,
//                trailingIcon = trailingIcon,
//                supportingText = supportingText,
//                shape = shape,
//                singleLine = singleLine,
//                enabled = enabled,
//                isError = isError,
//                interactionSource = interactionSource,
//                colors = TextFieldDefaults.textFieldColors(
//                    textColor = colors.textColor,
//                    containerColor = colors.containerColor,
//                    cursorColor = colors.cursorColor,
//                    focusedIndicatorColor = colors.focusedIndicatorColor,
//                    focusedSupportingTextColor = colors.focusedSupportingTextColor,
//                    focusedLabelColor = colors.focusedLabelColor,
//                )
//            )
//        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CustomTextFieldPreviews() {
    CustomTextField(
        value = "Hello",
        onValueChange = {}
    )
}