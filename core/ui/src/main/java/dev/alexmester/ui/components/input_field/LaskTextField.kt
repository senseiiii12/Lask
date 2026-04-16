package dev.alexmester.ui.components.input_field

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
fun LaskTextField(
    modifier: Modifier = Modifier,
    text: String,
    placeholderText: String,
    leadingIcon: ImageVector,
    onValueChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onDone: () -> Unit,
) {
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.LaskTypography.body2,
        modifier = modifier,
        placeholder = {
            Text(
                text = placeholderText,
                style = MaterialTheme.LaskTypography.body2,
                color = MaterialTheme.LaskColors.textSecondary,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.LaskColors.textSecondary,
            )
        },
        trailingIcon = {
            if (text.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        tint = MaterialTheme.LaskColors.textSecondary,
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.LaskColors.brand_blue,
            unfocusedBorderColor = MaterialTheme.LaskColors.brand_blue10,
            focusedTextColor = MaterialTheme.LaskColors.textPrimary,
            unfocusedTextColor = MaterialTheme.LaskColors.textPrimary,
            cursorColor = MaterialTheme.LaskColors.brand_blue,
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone() }
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    )
}