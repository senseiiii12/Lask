package dev.alexmester.impl.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.alexmester.api.navigation.ArticleDetailApi
import dev.alexmester.api.navigation.ArticleListRoute
import dev.alexmester.api.navigation.InterestsRoute
import dev.alexmester.api.navigation.LocalePickerRoute
import dev.alexmester.api.navigation.ProfileApi
import dev.alexmester.api.navigation.ProfileRoute
import dev.alexmester.api.navigation.SystemRoute
import dev.alexmester.impl.presentation.article_list.ArticleListScreen
import dev.alexmester.impl.presentation.interests.InterestsScreen
import dev.alexmester.impl.presentation.locale_picker.LocalePickerScreen
import dev.alexmester.impl.presentation.profile.ProfileScreen
import dev.alexmester.impl.presentation.system.SystemScreen
import dev.alexmester.ui.transition.SharedTransitionLocals

class ProfileImpl(
    private val articleDetailApi: ArticleDetailApi,
) : ProfileApi {

    override fun profileRoute() = ProfileRoute

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
    ) {

        navGraphBuilder.composable<ProfileRoute> {
            CompositionLocalProvider(
                SharedTransitionLocals.LocalAnimatedVisibilityScope provides this,
            ) {
                ProfileScreen(
                    onNavigateToArticleList = { type ->
                        navController.navigate(ArticleListRoute(type))
                    },
                    onNavigateToSystemSettings = {
                        navController.navigate(SystemRoute)
                    },
                    onNavigateToInterests = {
                        navController.navigate(InterestsRoute)
                    }
                )
            }
        }

        navGraphBuilder.composable<ArticleListRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ArticleListRoute>()
            CompositionLocalProvider(
                SharedTransitionLocals.LocalAnimatedVisibilityScope provides this,
            ) {
                ArticleListScreen(
                    type = route.type,
                    onBack = { navController.navigateUp() },
                    onArticleClick = { id, url ->
                        navController.navigate(
                            articleDetailApi.articleDetailRoute(
                                articleId = id,
                                articleUrl = url,
                            )
                        )
                    },
                )
            }
        }

        navGraphBuilder.composable<LocalePickerRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<LocalePickerRoute>()
            LocalePickerScreen(
                type = route.type,
                onBack = { navController.navigateUp() },
                onCountryOverride = { country ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("country_override", country)
                },
                onLanguageOverride = { language ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("language_override", language)
                },
            )
        }

        navGraphBuilder.composable<SystemRoute> {
            val countryOverride = it.savedStateHandle.remove<String>("country_override")
            val languageOverride = it.savedStateHandle.remove<String>("language_override")

            SystemScreen(
                onBack = { navController.navigateUp() },
                onNavigateToLocalePicker = { type ->
                    navController.navigate(LocalePickerRoute(type))
                },
                countryOverride = countryOverride,
                languageOverride = languageOverride,
            )
        }

        navGraphBuilder.composable<InterestsRoute> {
            InterestsScreen(
                onBack = { navController.navigateUp() }
            )
        }
    }
}