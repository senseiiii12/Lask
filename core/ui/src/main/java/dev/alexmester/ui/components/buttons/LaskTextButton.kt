package dev.alexmester.ui.components.buttons

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
fun LaskTextButton(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    onClick: () -> Unit,
    isEnable: Boolean = true,
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        enabled = isEnable,
    ) {
        Text(
            text = text,
            style = MaterialTheme.LaskTypography.button1,
            color = textColor
        )
    }
}