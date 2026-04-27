package dev.alexmester.impl.presentstion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExploreOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.snackbar.snackswipe.SnackSwipeBox
import dev.alexmester.impl.presentstion.components.ExploreList
import dev.alexmester.impl.presentstion.components.ExploreTopBar
import dev.alexmester.impl.presentstion.mvi.ExploreIntent
import dev.alexmester.impl.presentstion.mvi.ExploreSideEffect
import dev.alexmester.impl.presentstion.mvi.ExploreState
import dev.alexmester.impl.presentstion.mvi.ExploreViewModel
import dev.alexmester.ui.R
import dev.alexmester.ui.components.notification_screen.LaskNotificationScreen
import dev.alexmester.ui.components.notification_screen.NotificationType
import dev.alexmester.ui.components.pull_to_refresh_box.LaskPullToRefreshBox
import dev.alexmester.ui.components.snackbar.showErrorSnackbar
import dev.alexmester.ui.components.snackbar.showWarningSnackbar
import dev.alexmester.ui.desing_system.LaskColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = koinViewModel(),
    onArticleClick: (articleId: Long, articleUrl: String) -> Unit,
    onSearch: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val readArticleIds by viewModel.readArticleIds.collectAsStateWithLifecycle()
    val stateRefreshBox = rememberPullToRefreshState()
    val backgroundColorSnackError = MaterialTheme.LaskColors.error
    val backgroundColorSnackWarning = MaterialTheme.LaskColors.brand_blue10
    val context = LocalContext.current

    SnackSwipeBox(
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
    ) { snackSwipeController ->
        LaunchedEffect(Unit) {
            viewModel.sideEffects.collect { effect ->
                when (effect) {
                    is ExploreSideEffect.NavigateToArticle ->
                        onArticleClick(effect.articleId, effect.articleUrl)
                    is ExploreSideEffect.ShowError -> {
                        snackSwipeController.showErrorSnackbar(
                            backgroundColor = backgroundColorSnackError,
                            text = effect.message.asString(context)
                        )
                    }
                    is ExploreSideEffect.ShowWarning -> {
                        snackSwipeController.showWarningSnackbar(
                            backgroundColor = backgroundColorSnackWarning,
                            text = effect.message.asString(context)
                        )
                    }
                }
            }
        }
        ExploreScreenContent(
            state = state,
            readArticleIds = readArticleIds,
            onIntent = viewModel::handleIntent,
            stateRefreshBox = stateRefreshBox,
            onSearch = onSearch
        )
    }
}

@Composable
private fun ExploreScreenContent(
    state: ExploreState,
    readArticleIds: Set<Long>,
    stateRefreshBox: PullToRefreshState,
    onIntent: (ExploreIntent) -> Unit,
    onSearch: () -> Unit,
) {
    Scaffold(
        topBar = {
            ExploreTopBar(onSearch = onSearch)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(paddingValues)
        ) {
            when (state) {
                is ExploreState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.LaskColors.brand_blue10,
                        trackColor = MaterialTheme.LaskColors.brand_blue,
                    )
                }

                is ExploreState.Error -> {
                    LaskNotificationScreen(
                        type = NotificationType.Error(state.errorType),
                        showRetry = true,
                        isRetrying = state.isRefreshing,
                        onRetry = { onIntent(ExploreIntent.Refresh) },
                    )
                }

                is ExploreState.EmptyInterests -> {
                    LaskNotificationScreen(
                        type = NotificationType.Warning(
                            text = stringResource(R.string.interests_empty_message),
                            image = Icons.Default.ExploreOff
                        ),
                        showRetry = true,
                        isRetrying = state.isRefreshing,
                        onRetry = { onIntent(ExploreIntent.Refresh) },
                    )
                }

                is ExploreState.Content -> {
                    LaskPullToRefreshBox(
                        modifier = Modifier.fillMaxSize(),
                        isRefreshing = state.isRefreshing,
                        onRefresh = { onIntent(ExploreIntent.Refresh) },
                        state = stateRefreshBox,
                    ) {
                        ExploreList(
                            state = state,
                            readArticleIds = readArticleIds,
                            bottomPadding = paddingValues.calculateBottomPadding(),
                            onIntent = onIntent,
                        )
                    }
                }
            }
        }
    }
}
