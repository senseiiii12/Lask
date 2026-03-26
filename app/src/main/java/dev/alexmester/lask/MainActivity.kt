package dev.alexmester.lask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskPalette
import dev.alexmester.ui.desing_system.LaskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LaskTheme {
                val navController = rememberNavController()
                RootScreen(navController)
            }
        }
    }
}

