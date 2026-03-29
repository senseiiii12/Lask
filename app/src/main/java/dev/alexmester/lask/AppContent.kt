package dev.alexmester.lask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import dev.alexmester.api.navigation.FeedRoute
import dev.alexmester.lask.welcome_screen.SplashState
import dev.alexmester.lask.welcome_screen.SplashViewModel
import dev.alexmester.lask.welcome_screen.WelcomeRoute
import dev.alexmester.ui.desing_system.LaskTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppContent(
    splashViewModel: SplashViewModel = koinViewModel(),
) {
    val splashState by splashViewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(splashState) {
        val state = splashState
        if (state is SplashState.Ready) {
            splashViewModel.initLocaleIfNeeded(
                context = context,
                isManuallySet = state.isLocaleManuallySet,
            )
        }
    }

    LaskTheme {
        when (val state = splashState) {
            SplashState.Loading -> Unit
            is SplashState.Ready -> {
                val navController = rememberNavController()
                RootScreen(
                    navController = navController,
                    startDestination = if (state.isOnboardingCompleted) FeedRoute else WelcomeRoute,
                    onOnboardingComplete = { splashViewModel.completeOnboarding() },
                )
            }
        }
    }
}