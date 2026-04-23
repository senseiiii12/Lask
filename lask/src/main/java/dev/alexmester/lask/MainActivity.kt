package dev.alexmester.lask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.alexmester.lask.splash_screen.SplashState
import dev.alexmester.lask.splash_screen.SplashViewModel
import dev.alexmester.ui.desing_system.LaskPalette
import dev.alexmester.utils.locale.LocaleChangeObserver
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private lateinit var localeChangeObserver: LocaleChangeObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = LaskPalette.BackgroundPrimaryDark.toArgb(),
                darkScrim = LaskPalette.BackgroundPrimaryLight.toArgb()
            )
        )

        localeChangeObserver = LocaleChangeObserver(
            context = applicationContext,
            deviceLocaleProvider = get(),
            preferencesDataSource = get(),
            scope = get(),
        )
        val splashViewModel: SplashViewModel by viewModel()

        splashScreen.setKeepOnScreenCondition {
            splashViewModel.state.value is SplashState.Loading ||
            splashViewModel.state.value is SplashState.Initializing
        }

        setContent {
            AppContent(splashViewModel = splashViewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        localeChangeObserver.register()
        localeChangeObserver.checkAndUpdate()
    }

    override fun onPause() {
        super.onPause()
        localeChangeObserver.unregister()
    }
}

