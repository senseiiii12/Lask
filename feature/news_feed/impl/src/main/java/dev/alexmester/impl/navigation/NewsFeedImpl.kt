package dev.alexmester.impl.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.alexmester.api.navigation.FeedRoute
import dev.alexmester.api.navigation.NewsFeedApi

/**
 * Реализация NewsFeedApi.
 * Знает только о своих экранах.
 * Регистрируется в Koin в :app:
 * ```kotlin
 * single<NewsFeedApi> { NewsFeedImpl() }
 * ```
 */
class NewsFeedImpl : NewsFeedApi {

    override fun feedRoute() = FeedRoute

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
    ) {
        navGraphBuilder.composable<FeedRoute> {
//            NewsFeedScreen(navController = navController)
        }
    }
}