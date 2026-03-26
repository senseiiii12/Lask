package dev.alexmester.newsfeed.impl.presentation.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import dev.alexmester.impl.presentation.NewsFeedViewModel
import dev.alexmester.impl.presentation.components.NewsFeedList
import dev.alexmester.ui.components.pull_to_refresh_box.LaskPullToRefreshBox
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun NewsFeedScreen(
    navController: NavHostController,
    viewModel: NewsFeedViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val stateRefreshBox = rememberPullToRefreshState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is NewsFeedSideEffect.ShowError ->
                    snackbarHostState.showSnackbar(effect.message.asString(context))
                is NewsFeedSideEffect.NavigateToArticle ->{
//                    navController.navigate(...)
                }
            }
        }
    }

    NewsFeedScreenContent(
        modifier = Modifier,
        state = state,
        stateRefreshBox = stateRefreshBox,
        snackbarHostState = snackbarHostState,
        onRefresh = { viewModel.handleIntent(NewsFeedIntent.Refresh) },
        onArticleClick = { articleId, articleUrl ->
            viewModel.handleIntent(NewsFeedIntent.ArticleClick(
                articleId = articleId,
                articleUrl = articleUrl
            ))
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewsFeedScreenContent(
    modifier: Modifier,
    state: NewsFeedScreenState,
    stateRefreshBox: PullToRefreshState,
    snackbarHostState: SnackbarHostState,
    onRefresh: () -> Unit,
    onArticleClick: (articleId: Long, articleUrl: String) -> Unit,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Lask",
                        style = MaterialTheme.LaskTypography.h3,
                        color = MaterialTheme.LaskColors.textPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.LaskColors.brand_blue10
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                        isRefreshing = currentState.contentState is ContentState.Refreshing,
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
        }
    }
}





