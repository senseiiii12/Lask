package dev.alexmester.impl.presentation

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.snackbar.snackswipe.SnackSwipeBox
import dev.alexmester.error.NetworkErrorUiMapper
import dev.alexmester.impl.presentation.components.ArticleDetailContent
import dev.alexmester.impl.presentation.components.bottom_bar.ArticleDetailBottomBar
import dev.alexmester.impl.presentation.mvi.ArticleDetailIntent
import dev.alexmester.impl.presentation.mvi.ArticleDetailSideEffect
import dev.alexmester.impl.presentation.mvi.ArticleDetailState
import dev.alexmester.impl.presentation.mvi.ArticleDetailViewModel
import dev.alexmester.impl.presentation.mvi.contentOrNull
import dev.alexmester.impl.presentation.mvi.isContent
import dev.alexmester.ui.R
import dev.alexmester.ui.components.notification_screen.LaskNotificationScreen
import dev.alexmester.ui.components.notification_screen.NotificationType
import dev.alexmester.ui.components.snackbar.showBookmarkSnackbar
import dev.alexmester.ui.components.snackbar.showErrorSnackbar
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.uitext.UiText
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val READ_TIME_THRESHOLD_MS = 5_000L

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
    val backgroundColorSnackDefault = MaterialTheme.LaskColors.brand_blue10
    val backgroundColorSnackError = MaterialTheme.LaskColors.error
    val context = LocalContext.current

    LaunchedEffect(state.isContent) {
        if (!state.isContent) return@LaunchedEffect
        delay(READ_TIME_THRESHOLD_MS)
        viewModel.handleIntent(ArticleDetailIntent.TimeThresholdReached)
    }

    SnackSwipeBox(
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
    ) { snackSwipeController ->
        LaunchedEffect(Unit) {
            viewModel.sideEffects.collect { effect ->
                when (effect) {
                    is ArticleDetailSideEffect.NavigateBack -> onBack()
                    is ArticleDetailSideEffect.ShawBookmarkActionMessage -> {
                        snackSwipeController.showBookmarkSnackbar(
                            isBookmarked = effect.isBookmarked,
                            backgroundColor = backgroundColorSnackDefault,
                            addedText = UiText.StringResource(R.string.bookmark_add).asString(context),
                            removedText = UiText.StringResource(R.string.bookmark_removed).asString(context),
                        )
                    }
                    is ArticleDetailSideEffect.ShowError -> {
                        snackSwipeController.showErrorSnackbar(
                            backgroundColor = backgroundColorSnackError,
                            text = NetworkErrorUiMapper.toUiText(effect.errorType).asString(context)
                        )
                    }
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
        )
    }
}

@Composable
internal fun ArticleDetailScreenContent(
    modifier: Modifier = Modifier,
    state: ArticleDetailState,
    onIntent: (ArticleDetailIntent) -> Unit,
) {
    val hazeState = rememberHazeState()

    Scaffold(
        containerColor = MaterialTheme.LaskColors.backgroundPrimary,
        contentWindowInsets = WindowInsets(top = 0),
        bottomBar = {
            state.contentOrNull?.let { contentState ->
                ArticleDetailBottomBar(
                    contentState = contentState,
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
                    LaskNotificationScreen(
                        type = NotificationType.Warning(
                            text = state.message.asString(),
                            image = Icons.Default.Article
                        ),
                    )
                }

                is ArticleDetailState.Content -> {
                    ArticleDetailContent(
                        article = state.article,
                        bottomPadding = paddingValues.calculateBottomPadding(),
                        translatedTitle = state.translatedTitle,
                        translatedText = state.translatedText,
                        translationState = state.translationState,
                        onScrollThresholdReached = {
                            onIntent(ArticleDetailIntent.ScrollThresholdReached)
                        },
                    )
                }
            }
        }
    }
}


