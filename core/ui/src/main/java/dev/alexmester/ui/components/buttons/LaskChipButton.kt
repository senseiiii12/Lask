package dev.alexmester.ui.components.buttons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.R
import dev.alexmester.ui.components.locale.countryCodeToFlagEmoji
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskPalette
import dev.alexmester.ui.desing_system.LaskTheme
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
fun LaskChipButton(
    modifier: Modifier = Modifier,
    text: String,
    leadingCountryIcon: String? = null,
    leadingIcon: ImageVector? = null,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val shape = RoundedCornerShape(50.dp)
    val background = if (isSelected) MaterialTheme.LaskColors.brand_blue
        else MaterialTheme.LaskColors.backgroundPrimary
    val borderColor = if (isSelected) Color.Transparent else MaterialTheme.LaskColors.brand_blue10

    Row(
        modifier = modifier
            .clip(shape)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .background(background)
            .clickable { onClick() }
            .padding(vertical = 8.dp)
            .padding(start = 16.dp,end = 8.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedContent(targetState = isSelected) { isSelected ->
            if (isSelected) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    leadingCountryIcon?.let {
                        Text(
                            text = leadingCountryIcon,
                            style = MaterialTheme.LaskTypography.footnote,
                        )
                    }
                    leadingIcon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            tint = LaskPalette.TextPrimaryDark
                        )
                    }
                    Text(
                        text = text,
                        style = MaterialTheme.LaskTypography.body1SemiBold,
                        color = MaterialTheme.LaskColors.textPrimary
                    )
                    Icon(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .clickable { onDismiss() },
                        imageVector = ImageVector.vectorResource(R.drawable.ic_cancel),
                        contentDescription = null,
                        tint = LaskPalette.TextPrimaryDark
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.LaskTypography.body1SemiBold,
                        color = MaterialTheme.LaskColors.textPrimary
                    )
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.LaskColors.textPrimary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LaskChipButtonPreviewSeleceted1() {
    LaskTheme(darkTheme = true) {
        LaskChipButton(
            modifier = Modifier,
            text = "United State",
            leadingCountryIcon = countryCodeToFlagEmoji("us"),
            leadingIcon = null,
            isSelected = true,
            onClick = { },
            onDismiss = { }
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun LaskChipButtonPreviewSeleceted2() {
    LaskTheme(darkTheme = true) {
        LaskChipButton(
            modifier = Modifier,
            text = "Science",
            leadingCountryIcon = null,
            leadingIcon = Icons.Default.Category,
            isSelected = true,
            onClick = { },
            onDismiss = { }
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun LaskChipButtonPreviewUnSeleceted() {
    LaskTheme(darkTheme = false) {
        LaskChipButton(
            modifier = Modifier,
            text = "Country",
            leadingCountryIcon = null,
            leadingIcon = null,
            isSelected = false,
            onClick = { },
            onDismiss = { }
        )
    }
}