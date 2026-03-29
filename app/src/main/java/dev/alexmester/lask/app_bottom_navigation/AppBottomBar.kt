package dev.alexmester.lask.app_bottom_navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import dev.alexmester.ui.components.bottom_bar.BottomBarItem
import dev.alexmester.ui.components.bottom_bar.LaskBottomBar
import dev.chrisbanes.haze.HazeState

@Composable
fun AppBottomBar(
    navController: NavHostController,
    hazeState: HazeState,
) {
    val tabs = remember { AppTabs.getTabs() }
    var selectedTab by remember { mutableStateOf(tabs.first().route) }

    LaskBottomBar(
        hazeState = hazeState,
        items = tabs.map { tab ->
            BottomBarItem(
                icon = ImageVector.vectorResource(tab.iconRes),
                title = stringResource(tab.titleRes),
                isSelected = tab.route == selectedTab,
                onClick = {
                    if (tab.route == selectedTab) return@BottomBarItem
                    selectedTab = tab.route
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    )
}