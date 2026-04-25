package dev.alexmester.impl.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.alexmester.api.navigation.ArticleDetailApi
import dev.alexmester.api.navigation.NewsFeedRoute
import dev.alexmester.api.navigation.NewsFeedApi
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedScreen
import dev.alexmester.ui.shared_transition.SharedTransitionLocals

@OptIn(ExperimentalSharedTransitionApi::class)
class NewsFeedImpl(
    val articleDetailApi: ArticleDetailApi,
) : NewsFeedApi {

    override fun feedRoute() = NewsFeedRoute

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
    ) {
        navGraphBuilder.composable<NewsFeedRoute> {
            CompositionLocalProvider(
                SharedTransitionLocals.LocalAnimatedVisibilityScope provides this,
            ) {
                NewsFeedScreen(
                    onArticleClick = { id, url ->
                        navController.navigate(
                            articleDetailApi.articleDetailRoute(
                                articleId = id,
                                articleUrl = url
                            )
                        )
                    }
                )
            }
        }
    }
}