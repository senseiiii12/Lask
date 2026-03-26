package dev.alexmester.lask

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dev.alexmester.api.navigation.BookMarkRoute
import dev.alexmester.api.navigation.ExploreRoute
import dev.alexmester.api.navigation.FeedRoute
import dev.alexmester.api.navigation.NewsFeedApi
import dev.alexmester.api.navigation.ProfileRoute
import dev.alexmester.lask.navigation.BottomTab
import dev.alexmester.navigation.register
import dev.alexmester.ui.components.bottom_bar.BottomBarItem
import dev.alexmester.ui.components.bottom_bar.LaskBottomBar
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import org.koin.compose.koinInject

@Composable
fun RootScreen(
    navController: NavHostController
) {
    val newsFeedApi = koinInject<NewsFeedApi>()

    val tabs = listOf(
        BottomTab(Icons.Default.Home, "Home", FeedRoute),
        BottomTab(Icons.Default.Explore, "Explore", ExploreRoute),
        BottomTab(Icons.Default.Bookmark, "Bookmark", BookMarkRoute),
        BottomTab(Icons.Default.Person, "Profile", ProfileRoute)
    )

    var selectedTab by remember {
        mutableStateOf(tabs.first().route)
    }
    val hazeState = rememberHazeState()
    Scaffold(
        modifier = Modifier,
        bottomBar = {
            LaskBottomBar(
                modifier = Modifier,
                hazeState = hazeState,
                items = tabs.map { tab ->
                    BottomBarItem(
                        icon = tab.icon,
                        title = tab.title,
                        isSelected = tab.route == selectedTab,
                        onClick = {
                            selectedTab = tab.route

                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
//                                restoreState = true
                            }
                        }
                    )
                }
            )
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = FeedRoute,
            modifier = Modifier.hazeSource(hazeState)
        ) {
            register(newsFeedApi, navController)
        }
    }
}