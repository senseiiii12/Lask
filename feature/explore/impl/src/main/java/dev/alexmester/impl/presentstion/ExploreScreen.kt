package dev.alexmester.impl.presentstion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExploreOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.impl.presentstion.components.ExploreList
import dev.alexmester.impl.presentstion.components.ExploreTopBar
import dev.alexmester.impl.presentstion.mvi.ExploreIntent
import dev.alexmester.impl.presentstion.mvi.ExploreSideEffect
import dev.alexmester.impl.presentstion.mvi.ExploreState
import dev.alexmester.impl.presentstion.mvi.ExploreViewModel
import dev.alexmester.ui.R
import dev.alexmester.ui.components.notification_screen.LaskNotificationScreen
import dev.alexmester.ui.components.notification_screen.LayoutVariants
import dev.alexmester.ui.components.pull_to_refresh_box.LaskPullToRefreshBox
import dev.alexmester.ui.components.snackbar.LaskTopSnackbarHost
import dev.alexmester.ui.components.snackbar.showLaskSnackbar
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
    val snackbarHostState = remember { SnackbarHostState() }
    val stateRefreshBox = rememberPullToRefreshState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is ExploreSideEffect.NavigateToArticle ->
                    onArticleClick(effect.articleId, effect.articleUrl)
                is ExploreSideEffect.ShowError -> {
                    snackbarHostState.showLaskSnackbar(
                        message = effect.message.asString(context),
                        isError = true,
                    )
                }
            }
        }
    }

    ExploreScreenContent(
        state = state,
        readArticleIds = readArticleIds,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::handleIntent,
        stateRefreshBox = stateRefreshBox,
        onSearch = onSearch
    )
}

@Composable
private fun ExploreScreenContent(
    state: ExploreState,
    readArticleIds: Set<Long>,
    snackbarHostState: SnackbarHostState,
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
                        errorType = state.errorType,
                        isRetrying = state.isRefreshing,
                        onRetry = { onIntent(ExploreIntent.Refresh) },
                    )
                }

                is ExploreState.EmptyInterests -> {
                    LaskNotificationScreen(
                        imageWarning = Icons.Default.ExploreOff,
                        textWarning = stringResource(R.string.interests_empty_message),
                        layoutVariants = LayoutVariants.WARNING,
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

            LaskTopSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
            )
        }
    }
}