package dev.alexmester.lask.app_bottom_navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.alexmester.api.navigation.ArticleDetailRoute
import dev.alexmester.api.navigation.ArticleListRoute
import dev.alexmester.lask.welcome_screen.WelcomeRoute
import dev.alexmester.ui.components.bottom_bar.LaskMainBottomBarItem
import dev.alexmester.ui.components.bottom_bar.LaskBottomBar
import dev.chrisbanes.haze.HazeState

@Composable
fun AppBottomBar(
    navController: NavHostController,
    hazeState: HazeState,
) {
    val tabs = remember { AppTabs.getTabs() }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val bottomBarDestination = currentBackStackEntry?.destination

     if (bottomBarDestination.shouldShowBottomBar()){
         LaskBottomBar(
             hazeState = hazeState,
             items = tabs.map { tab ->

                 val isSelected = bottomBarDestination
                     ?.hierarchy
                     ?.any { it.route?.contains(tab.route::class.qualifiedName!!) == true } == true

                 LaskMainBottomBarItem(
                     icon = ImageVector.vectorResource(tab.iconRes),
                     title = stringResource(tab.titleRes),
                     isSelected = isSelected,
                     onClick = {
                         if (isSelected) return@LaskMainBottomBarItem
                         navController.navigate(tab.route) {
                             popUpTo(navController.graph.startDestinationId) {
                                 saveState = true
                             }
                             launchSingleTop = true
                             restoreState = true
                         }
                     }
                 )
             }
         )
     }
}

fun NavDestination?.shouldShowBottomBar(): Boolean {
    if (this == null) return false

    return hierarchy.none {
        it.route?.contains(ArticleDetailRoute::class.qualifiedName!!) == true ||
        it.route?.contains(WelcomeRoute::class.qualifiedName!!) == true ||
        it.route?.contains(ArticleListRoute::class.qualifiedName!!) == true
    }
}