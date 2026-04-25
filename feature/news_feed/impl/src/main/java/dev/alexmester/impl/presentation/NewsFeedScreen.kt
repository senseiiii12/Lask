package dev.alexmester.newsfeed.impl.presentation.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.snackbar.snackswipe.SnackSwipeBox
import dev.alexmester.error.NetworkErrorUiMapper
import dev.alexmester.impl.presentation.components.NewsFeedEmptyScreen
import dev.alexmester.impl.presentation.components.NewsFeedList
import dev.alexmester.impl.presentation.components.NewsFeedTopBar
import dev.alexmester.impl.presentation.mvi.NewsFeedViewModel
import dev.alexmester.newsfeed.impl.presentation.components.NewsFeedOfflineBanner
import dev.alexmester.ui.components.notification_screen.LaskNotificationScreen
import dev.alexmester.ui.components.notification_screen.NotificationType
import dev.alexmester.ui.components.pull_to_refresh_box.LaskPullToRefreshBox
import dev.alexmester.ui.components.snackbar.showErrorSnackbar
import dev.alexmester.ui.desing_system.LaskColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewsFeedScreen(
    viewModel: NewsFeedViewModel = koinViewModel(),
    onArticleClick: (articleId: Long, articleUrl: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val readArticleIds by viewModel.readArticleIds.collectAsStateWithLifecycle()
    val stateRefreshBox = rememberPullToRefreshState()
    val backgroundColorSnack = MaterialTheme.LaskColors.error
    val context = LocalContext.current

    SnackSwipeBox(
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
    ) { snackSwipeController ->
        LaunchedEffect(Unit) {
            viewModel.sideEffects.collect { effect ->
                when (effect) {
                    is NewsFeedSideEffect.ShowError -> {
                        if (state.isContent) {
                            val messageError = NetworkErrorUiMapper.toUiText(effect.message).asString(context)
                            snackSwipeController.showErrorSnackbar(
                                backgroundColor = backgroundColorSnack,
                                text = messageError
                            )
                        }
                    }
                    is NewsFeedSideEffect.NavigateToArticle -> {
                        onArticleClick(effect.articleId, effect.articleUrl)
                    }
                }
            }
        }
        NewsFeedScreenContent(
            modifier = Modifier,
            state = state,
            readArticleIds = readArticleIds,
            stateRefreshBox = stateRefreshBox,
            onIntent = viewModel::handleIntent
        )
    }
}

@Composable
internal fun NewsFeedScreenContent(
    modifier: Modifier,
    state: NewsFeedState,
    readArticleIds: Set<Long>,
    stateRefreshBox: PullToRefreshState,
    onIntent: (NewsFeedIntent) -> Unit,
) {
    Scaffold(
        topBar = { NewsFeedTopBar(state = state) }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(paddingValues)
        ) {
            when (val currentState = state) {

                is NewsFeedState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.LaskColors.brand_blue10,
                        trackColor = MaterialTheme.LaskColors.brand_blue
                    )
                }
                is NewsFeedState.Empty -> {
                    LaskPullToRefreshBox(
                        modifier = Modifier.fillMaxSize(),
                        isRefreshing = currentState.isRefreshing,
                        onRefresh = { onIntent(NewsFeedIntent.Refresh) },
                        state = stateRefreshBox,
                    ) {
                        NewsFeedEmptyScreen(
                            country = currentState.country,
                            language = currentState.language,
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                        )
                    }
                }
                is NewsFeedState.Error -> {
                    LaskNotificationScreen(
                        modifier = Modifier,
                        type = NotificationType.Error(currentState.errorType),
                        showRetry = true,
                        isRetrying = currentState.isRefreshing,
                        onRetry = { onIntent(NewsFeedIntent.Refresh) }
                    )
                }

                is NewsFeedState.Content -> {
                    LaskPullToRefreshBox(
                        modifier = Modifier.fillMaxSize(),
                        isRefreshing = currentState.isRefreshing,
                        onRefresh = { onIntent(NewsFeedIntent.Refresh) },
                        state = stateRefreshBox,
                    ) {
                        Column {
                            AnimatedVisibility(visible = state.isOffline) {
                                if (state.contentState is ContentState.Offline) {
                                    NewsFeedOfflineBanner(
                                        lastCachedAt = state.contentState.lastCachedAt
                                    )
                                }
                            }
                            NewsFeedList(
                                modifier = Modifier,
                                state = currentState,
                                readArticleIds = readArticleIds,
                                bottomPadding = paddingValues.calculateBottomPadding(),
                                onClickArticle = { artilceId, arlicteUrl ->
                                    onIntent(
                                        NewsFeedIntent.ArticleClick(
                                            articleId = artilceId,
                                            articleUrl = arlicteUrl
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}








