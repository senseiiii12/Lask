package dev.alexmester.impl.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.alexmester.api.navigation.ArticleDetailApi
import dev.alexmester.api.navigation.ArticleListRoute
import dev.alexmester.api.navigation.ProfileApi
import dev.alexmester.api.navigation.ProfileRoute
import dev.alexmester.impl.presentation.ProfileScreen

class ProfileImpl(
    private val articleDetailApi: ArticleDetailApi,
) : ProfileApi {

    override fun profileRoute() = ProfileRoute

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
    ) {
        navGraphBuilder.composable<ProfileRoute> {
            ProfileScreen(
                onNavigateToArticleList = { type ->
                    navController.navigate(ArticleListRoute(type))
                },
            )
        }

//        navGraphBuilder.composable<ArticleListRoute> { backStackEntry ->
//            val route = backStackEntry.toRoute<ArticleListRoute>()
//            ArticleListScreen(
//                type = route.type,
//                onBack = { navController.navigateUp() },
//                onArticleClick = { id, url ->
//                    navController.navigate(
//                        articleDetailApi.articleDetailRoute(
//                            articleId = id,
//                            articleUrl = url,
//                        )
//                    )
//                },
//            )
//        }
    }
}