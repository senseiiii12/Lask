package dev.alexmester.newsfeed.impl.presentation.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.impl.presentation.NewsFeedViewModel
import dev.alexmester.impl.presentation.components.NewsFeedList
import dev.alexmester.impl.presentation.components.NewsFeedTopBar
import dev.alexmester.ui.components.pull_to_refresh_box.LaskPullToRefreshBox
import dev.alexmester.ui.components.snackbar.LaskTopSnackbarHost
import dev.alexmester.ui.components.snackbar.showLaskSnackbar
import dev.alexmester.ui.desing_system.LaskColors
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun NewsFeedScreen(
    viewModel: NewsFeedViewModel = koinViewModel(),
    onArticleClick: (articleId: Long, articleUrl: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val stateRefreshBox = rememberPullToRefreshState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is NewsFeedSideEffect.ShowError ->
                    snackbarHostState.showLaskSnackbar(
                        message = effect.message.asString(context),
                        isError = true,
                    )

                is NewsFeedSideEffect.NavigateToArticle -> {
                    onArticleClick(effect.articleId, effect.articleUrl)
                }
            }
        }
    }

    NewsFeedScreen(
        modifier = Modifier,
        state = state,
        stateRefreshBox = stateRefreshBox,
        snackbarHostState = snackbarHostState,
        onRefresh = { viewModel.handleIntent(NewsFeedIntent.Refresh) },
        onArticleClick = { articleId, articleUrl ->
            viewModel.handleIntent(
                NewsFeedIntent.ArticleClick(
                    articleId = articleId,
                    articleUrl = articleUrl
                )
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewsFeedScreen(
    modifier: Modifier,
    state: NewsFeedScreenState,
    stateRefreshBox: PullToRefreshState,
    snackbarHostState: SnackbarHostState,
    onRefresh: () -> Unit,
    onArticleClick: (articleId: Long, articleUrl: String) -> Unit,
) {

    Scaffold(
        topBar = { NewsFeedTopBar(state = state) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(paddingValues)
        ) {
            when (val currentState = state) {

                is NewsFeedScreenState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.LaskColors.brand_blue10,
                        trackColor = MaterialTheme.LaskColors.brand_blue
                    )
                }

                is NewsFeedScreenState.Error -> {
                    Text(
                        text = currentState.message.asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.LaskColors.error,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                is NewsFeedScreenState.Content -> {
                    LaskPullToRefreshBox(
                        modifier = Modifier.fillMaxSize(),
                        isRefreshing = currentState.isRefreshing,
                        onRefresh = onRefresh,
                        state = stateRefreshBox,
                    ) {
                        NewsFeedList(
                            modifier = Modifier,
                            state = currentState,
                            onClickArticle = { artilceId, arlicteUrl ->
                                onArticleClick(artilceId, arlicteUrl)
                            }
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

fun countryToFlag(countryCode: String): String {
    return countryCode
        .uppercase()
        .map { char -> Character.toCodePoint('\uD83C', '\uDDE6' + (char - 'A')) }
        .joinToString("") { String(Character.toChars(it)) }
}




