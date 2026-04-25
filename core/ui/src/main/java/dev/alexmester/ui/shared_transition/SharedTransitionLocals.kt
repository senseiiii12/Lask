package dev.alexmester.ui.shared_transition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf

@OptIn(ExperimentalSharedTransitionApi::class)
object SharedTransitionLocals {

    val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
    val LocalAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }
}