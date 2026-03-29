package dev.alexmester.lask.welcome_screen

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.alexmester.api.navigation.FeedRoute
import dev.alexmester.ui.components.welcome_screen.WelcomeScreen

fun NavGraphBuilder.welcomeScreen(
    navController: NavHostController,
    onOnboardingComplete: () -> Unit,
) {
    composable<WelcomeRoute> {
        WelcomeScreen(
            onExploreClick = {
                onOnboardingComplete()
                navController.navigate(FeedRoute) {
                    popUpTo(WelcomeRoute) { inclusive = true }
                }
            }
        )
    }
}