package dev.alexmester.ui.components.bottom_bar

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomBarItem(
    val icon: ImageVector,
    val title: String,
    val isSelected: Boolean,
    val onClick: () -> Unit
)