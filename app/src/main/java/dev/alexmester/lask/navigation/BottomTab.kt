package dev.alexmester.lask.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomTab(
    val icon: ImageVector,
    val title: String,
    val route: Any // type-safe route из feature api
)