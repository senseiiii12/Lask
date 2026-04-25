package dev.alexmester.impl.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.alexmester.api.navigation.ArticleDetailApi
import dev.alexmester.api.navigation.SearchApi
import dev.alexmester.api.navigation.SearchRoute
import dev.alexmester.impl.presentation.SearchScreen
import dev.alexmester.ui.shared_transition.SharedTransitionLocals

class SearchImpl(
    private val articleDetailApi: ArticleDetailApi,
) : SearchApi {

    override fun searchRoute() = SearchRoute

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
    ) {
        navGraphBuilder.composable<SearchRoute> {
            CompositionLocalProvider(
                SharedTransitionLocals.LocalAnimatedVisibilityScope provides this,
            ) {
                SearchScreen(
                    onCancel = { navController.navigateUp() },
                    onArticleClick = { id, url ->
                        navController.navigate(
                            articleDetailApi.articleDetailRoute(articleId = id, articleUrl = url)
                        )
                    }
                )
            }
        }
    }
}