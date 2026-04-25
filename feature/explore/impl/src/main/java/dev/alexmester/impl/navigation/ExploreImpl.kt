package dev.alexmester.impl.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.alexmester.api.navigation.ArticleDetailApi
import dev.alexmester.api.navigation.ExploreApi
import dev.alexmester.api.navigation.ExploreRoute
import dev.alexmester.api.navigation.SearchApi
import dev.alexmester.impl.presentstion.ExploreScreen
import dev.alexmester.ui.shared_transition.SharedTransitionLocals

@OptIn(ExperimentalSharedTransitionApi::class)
class ExploreImpl(
    private val articleDetailApi: ArticleDetailApi,
    private val searchApi: SearchApi,
) : ExploreApi {

    override fun exploreRoute() = ExploreRoute

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
    ) {
        navGraphBuilder.composable<ExploreRoute> {
            CompositionLocalProvider(
                SharedTransitionLocals.LocalAnimatedVisibilityScope provides this,
            ) {
                ExploreScreen(
                    onArticleClick = { id, url ->
                        navController.navigate(
                            articleDetailApi.articleDetailRoute(
                                articleId = id,
                                articleUrl = url,
                            )
                        )
                    },
                    onSearch = {
                        navController.navigate(searchApi.searchRoute())
                    }
                )
            }
        }
    }
}