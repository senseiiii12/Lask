package dev.alexmester.lask

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dev.alexmester.api.navigation.NewsFeedApi
import dev.alexmester.navigation.register
import org.koin.compose.koinInject

@Composable
fun MainScreen() {

//    // Получаем реализации через Koin — :app знает про impl, экраны получают только api
//    val newsFeedApi = koinInject<NewsFeedApi>()
//    // val exploreApi = koinInject<ExploreApi>()
//    // val bookmarksApi = koinInject<BookmarksApi>()
//    // val settingsApi = koinInject<SettingsApi>()
//    // val searchApi = koinInject<SearchApi>()
//    // val articleDetailApi = koinInject<ArticleDetailApi>()
//
//    NavHost(
//        navController = navController,
//        startDestination = newsFeedApi.feedRoute(),
//    ) {
//        register(newsFeedApi, navController)
//        // register(exploreApi, navController)
//        // register(bookmarksApi, navController)
//        // register(settingsApi, navController)
//        // register(searchApi, navController)
//        // register(articleDetailApi, navController)
//    }
}