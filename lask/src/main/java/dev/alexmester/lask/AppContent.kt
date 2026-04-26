package dev.alexmester.lask

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dev.alexmester.api.navigation.NewsFeedRoute
import dev.alexmester.lask.splash_screen.SplashState
import dev.alexmester.lask.splash_screen.SplashViewModel
import dev.alexmester.lask.theme_switch.ThemeViewModel
import dev.alexmester.lask.welcome_screen.WelcomeRoute
import dev.alexmester.ui.desing_system.LaskTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppContent(
    splashViewModel: SplashViewModel,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val splashState by splashViewModel.state.collectAsStateWithLifecycle()
    val state by themeViewModel.state.collectAsStateWithLifecycle()

    val darkTheme = when (state.isDarkTheme) {
        true -> true
        false -> false
        null -> isSystemInDarkTheme()
    }
    LaskTheme(darkTheme = darkTheme) {
        when (val state = splashState) {
            SplashState.Loading,
            SplashState.Initializing -> Unit

            is SplashState.Ready -> {
                val navController = rememberNavController()
                val startDestination = remember {
                    if (state.isOnboardingCompleted) NewsFeedRoute else WelcomeRoute
                }
                RootScreen(
                    navController = navController,
                    startDestination = startDestination,
                    onOnboardingComplete = {
                        splashViewModel.completeOnboarding()
                    },
                )
            }
        }
    }
}