package dev.alexmester.impl.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.alexmester.api.navigation.ArticleDetailApi
import dev.alexmester.api.navigation.BookmarkRoute
import dev.alexmester.api.navigation.BookmarksApi
import dev.alexmester.impl.presentation.BookmarksScreen
import dev.alexmester.ui.shared_transition.SharedTransitionLocals

class BookmarksImpl(
    private val articleDetailApi: ArticleDetailApi,
) : BookmarksApi {

    override fun bookmarkRoute() = BookmarkRoute

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
    ) {
        navGraphBuilder.composable<BookmarkRoute> {
            CompositionLocalProvider(
                SharedTransitionLocals.LocalAnimatedVisibilityScope provides this,
            ) {
                BookmarksScreen(
                    onArticleClick = { id, url ->
                        navController.navigate(
                            articleDetailApi.articleDetailRoute(
                                articleId = id,
                                articleUrl = url,
                            )
                        )
                    }
                )
            }
        }
    }
}