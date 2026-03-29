package dev.alexmester.lask.app_bottom_navigation

import dev.alexmester.api.navigation.BookMarkRoute
import dev.alexmester.api.navigation.ExploreRoute
import dev.alexmester.api.navigation.FeedRoute
import dev.alexmester.api.navigation.ProfileRoute
import dev.alexmester.ui.R

data class AppBottomTab(
    val iconRes: Int,
    val titleRes: Int,
    val route: Any,
)

object AppTabs {
    fun getTabs() = listOf(
        AppBottomTab(R.drawable.ic_trends,   R.string.tab_top_news, FeedRoute),
        AppBottomTab(R.drawable.ic_explore,  R.string.tab_explore, ExploreRoute),
        AppBottomTab(R.drawable.ic_bookmark, R.string.tab_bookmark, BookMarkRoute),
        AppBottomTab(R.drawable.ic_profile,  R.string.tab_profile, ProfileRoute),
    )
}