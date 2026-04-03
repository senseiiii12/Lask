package dev.alexmester.impl.presentation

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.impl.presentation.components.ArticleDetailBottomBar
import dev.alexmester.impl.presentation.components.ArticleDetailContent
import dev.alexmester.impl.presentation.mvi.ArticleDetailIntent
import dev.alexmester.impl.presentation.mvi.ArticleDetailSideEffect
import dev.alexmester.impl.presentation.mvi.ArticleDetailState
import dev.alexmester.impl.presentation.mvi.ArticleDetailViewModel
import dev.alexmester.impl.presentation.mvi.contentOrNull
import dev.alexmester.ui.components.snackbar.LaskTopSnackbarHost
import dev.alexmester.ui.components.snackbar.showLaskSnackbar
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val READ_TIME_THRESHOLD_MS = 10_000L

@Composable
fun ArticleDetailScreen(
    articleId: Long,
    articleUrl: String,
    onBack: () -> Unit,
    viewModel: ArticleDetailViewModel = koinViewModel(
        parameters = { parametersOf(articleId, articleUrl) }
    ),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val isContentReady = state is ArticleDetailState.Content

    LaunchedEffect(isContentReady) {
        if (state !is ArticleDetailState.Content) return@LaunchedEffect
        delay(READ_TIME_THRESHOLD_MS)
        viewModel.handleIntent(ArticleDetailIntent.TimeThresholdReached)
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is ArticleDetailSideEffect.NavigateBack -> onBack()
                is ArticleDetailSideEffect.ShowSnackbar ->
                    snackbarHostState.showLaskSnackbar(effect.message.asString(context))
                is ArticleDetailSideEffect.ShareUrl -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, effect.url)
                    }
                    context.startActivity(Intent.createChooser(intent, "Поделиться"))
                }
            }
        }
    }

    ArticleDetailScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState
    )
}

@Composable
internal fun ArticleDetailScreenContent(
    modifier: Modifier = Modifier,
    state: ArticleDetailState,
    onIntent: (ArticleDetailIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val hazeState = rememberHazeState()

    Scaffold(
        containerColor = MaterialTheme.LaskColors.backgroundPrimary,
        contentWindowInsets = WindowInsets(top = 0),
        bottomBar = {
            state.contentOrNull?.let { content ->
                ArticleDetailBottomBar(
                    isBookmarked = content.isBookmarked,
                    clapCount = content.clapCount,
                    isClapAnimating = content.isClapAnimating,
                    hazeState = hazeState,
                    onIntent = onIntent,
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .hazeSource(hazeState)
        ) {
            when (val state = state) {
                ArticleDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.LaskColors.brand_blue10,
                        trackColor = MaterialTheme.LaskColors.brand_blue
                    )
                }

                is ArticleDetailState.Error -> {
                    Text(
                        text = state.message.asString(),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.LaskTypography.body1,
                        color = MaterialTheme.LaskColors.error,
                    )
                }

                is ArticleDetailState.Content -> {
                    ArticleDetailContent(
                        article = state.article,
                        bottomPadding = paddingValues.calculateBottomPadding(),
                        onScrollThresholdReached = {
                            onIntent(ArticleDetailIntent.ScrollThresholdReached)
                        },
                    )
                }
            }

            LaskTopSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(top = 8.dp),
            )
        }
    }
}