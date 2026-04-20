package dev.alexmester.impl.presentation.article_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.api.navigation.ArticleListType
import dev.alexmester.impl.presentation.article_list.components.ArticleListContent
import dev.alexmester.impl.presentation.article_list.components.ArticleListTopBar
import dev.alexmester.impl.presentation.article_list.mvi.ArticleListIntent
import dev.alexmester.impl.presentation.article_list.mvi.ArticleListSideEffect
import dev.alexmester.impl.presentation.article_list.mvi.ArticleListState
import dev.alexmester.impl.presentation.article_list.mvi.ArticleListViewModel
import dev.alexmester.ui.R
import dev.alexmester.ui.components.notification_screen.LaskNotificationScreen
import dev.alexmester.ui.components.notification_screen.NotificationType
import dev.alexmester.ui.desing_system.LaskColors
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ArticleListScreen(
    type: ArticleListType,
    onBack: () -> Unit,
    onArticleClick: (articleId: Long, articleUrl: String) -> Unit,
    viewModel: ArticleListViewModel = koinViewModel(
        parameters = { parametersOf(type) }
    ),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is ArticleListSideEffect.NavigateBack -> onBack()
                is ArticleListSideEffect.NavigateToArticle ->
                    onArticleClick(effect.articleId, effect.articleUrl)
            }
        }
    }

    ArticleListScreenContent(
        type = type,
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ArticleListScreenContent(
    type: ArticleListType,
    state: ArticleListState,
    onIntent: (ArticleListIntent) -> Unit,
) {
    val title = when (type) {
        ArticleListType.READ -> stringResource(R.string.profile_menu_read_articles)
        ArticleListType.CLAPPED -> stringResource(R.string.profile_menu_clapped_articles)
    }

    Scaffold(
        topBar = {
            ArticleListTopBar(
                modifier = Modifier,
                title = title,
                onBack = { onIntent(ArticleListIntent.Back) }
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
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.LaskColors.brand_blue,
                        trackColor = MaterialTheme.LaskColors.brand_blue10,
                    )
                }

                state.allArticles.isEmpty() -> {
                    LaskNotificationScreen(
                        type = NotificationType.Warning(
                            text = stringResource(R.string.warning_no_articles),
                            image = Icons.Default.Article
                        )
                    )
                }

                else -> {
                    ArticleListContent(
                        modifier = Modifier,
                        state = state,
                        onIntent = { onIntent(it) }
                    )
                }
            }
        }
    }
}