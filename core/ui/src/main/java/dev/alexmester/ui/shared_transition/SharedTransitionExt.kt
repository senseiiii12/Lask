package dev.alexmester.ui.shared_transition

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.sharedElementIfAvailable(key: Any): Modifier {
    val sharedTransitionScope = SharedTransitionLocals.LocalSharedTransitionScope.current
    val animatedVisibilityScope = SharedTransitionLocals.LocalAnimatedVisibilityScope.current

    return if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            this@sharedElementIfAvailable.sharedElement(
                sharedContentState = rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedVisibilityScope,
            )
        }
    } else {
        this
    }
}