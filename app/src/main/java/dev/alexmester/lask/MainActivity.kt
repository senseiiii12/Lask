package dev.alexmester.lask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import dev.alexmester.api.navigation.FeedRoute
import dev.alexmester.lask.welcome_screen.SplashState
import dev.alexmester.lask.welcome_screen.SplashViewModel
import dev.alexmester.lask.welcome_screen.WelcomeRoute
import dev.alexmester.ui.desing_system.LaskTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val splashViewModel: SplashViewModel by viewModel()
        splashScreen.setKeepOnScreenCondition {
            splashViewModel.state.value is SplashState.Loading
        }

        setContent {
            AppContent(splashViewModel = splashViewModel)
        }
    }
}

