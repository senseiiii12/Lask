package dev.alexmester.impl.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.impl.presentation.components.BookmarksList
import dev.alexmester.impl.presentation.components.BookmarksTopBar
import dev.alexmester.impl.presentation.mvi.BookmarksIntent
import dev.alexmester.impl.presentation.mvi.BookmarksSideEffect
import dev.alexmester.impl.presentation.mvi.BookmarksState
import dev.alexmester.impl.presentation.mvi.BookmarksViewModel
import dev.alexmester.impl.presentation.mvi.contentOrNull
import dev.alexmester.ui.R
import dev.alexmester.ui.components.notification_screen.LaskNotificationScreen
import dev.alexmester.ui.components.notification_screen.NotificationType
import dev.alexmester.ui.desing_system.LaskColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookmarksScreen(
    onArticleClick: (articleId: Long, articleUrl: String) -> Unit,
    viewModel: BookmarksViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is BookmarksSideEffect.NavigateToArticle ->
                    onArticleClick(effect.articleId, effect.articleUrl)
            }
        }
    }

    BookmarksScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@Composable
internal fun BookmarksScreenContent(
    state: BookmarksState,
    onIntent: (BookmarksIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            BookmarksTopBar(
                isEditMode = state.contentOrNull?.isEditMode == true,
                onIntent = { onIntent(it) },
            )
        },
        containerColor = MaterialTheme.LaskColors.backgroundPrimary,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(paddingValues),
        ) {
            when (val currentState = state) {
                is BookmarksState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.LaskColors.brand_blue,
                        trackColor = MaterialTheme.LaskColors.brand_blue10,
                    )
                }

                is BookmarksState.Empty -> {
                    LaskNotificationScreen(
                        modifier = Modifier,
                        type = NotificationType.Warning(
                            text = stringResource(R.string.warning_empty_bookmarks),
                            image = Icons.Default.Bookmarks
                        )
                    )
                }

                is BookmarksState.Content -> {
                    BookmarksList(
                        articles = currentState.articles,
                        isEditMode = currentState.isEditMode,
                        pendingRemovalIds = currentState.pendingRemovalIds,
                        bottomPadding = paddingValues.calculateBottomPadding(),
                        onArticleClick = { id, url ->
                            if (!currentState.isEditMode) {
                                onIntent(BookmarksIntent.ArticleClick(id, url))
                            }
                        },
                        onTogglePendingRemoval = { id ->
                            onIntent(BookmarksIntent.TogglePendingRemoval(id))
                        },
                    )
                }
            }
        }
    }
}