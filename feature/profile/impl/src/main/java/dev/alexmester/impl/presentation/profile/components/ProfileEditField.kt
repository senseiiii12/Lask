package dev.alexmester.impl.presentation.profile.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
internal fun ProfileEditField(
    modifier: Modifier = Modifier,
    isEdit: Boolean,
    editName: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        modifier = modifier,
        value = editName,
        onValueChange = { onValueChange(it) },
        singleLine = true,
        textStyle = MaterialTheme.LaskTypography.h5.copy(
            color = MaterialTheme.LaskColors.textPrimary
        ),
        enabled = isEdit,
        cursorBrush = SolidColor(MaterialTheme.LaskColors.brand_blue),
    ) { innerTextField ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = editName,
            innerTextField = innerTextField,
            placeholder = {
                Text(
                    text = editName,
                    style = MaterialTheme.LaskTypography.h5,
                    color = MaterialTheme.LaskColors.textPrimary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            },
            enabled = isEdit,
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            interactionSource = remember { MutableInteractionSource() },
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
            container = {
                OutlinedTextFieldDefaults.Container(
                    enabled = isEdit,
                    isError = false,
                    interactionSource = remember { MutableInteractionSource() },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.LaskColors.brand_blue,
                        unfocusedBorderColor = MaterialTheme.LaskColors.brand_blue,
                    )
                )
            }
        )
    }
}