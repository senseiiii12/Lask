package dev.alexmester.impl.presentation.article_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.KeyboardBackspace
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.api.navigation.ArticleListType
import dev.alexmester.impl.presentation.article_list.components.ArticleListContent
import dev.alexmester.impl.presentation.article_list.components.ArticleListTopBar
import dev.alexmester.impl.presentation.article_list.components.CategoryFilterRow
import dev.alexmester.impl.presentation.article_list.mvi.ArticleListIntent
import dev.alexmester.impl.presentation.article_list.mvi.ArticleListSideEffect
import dev.alexmester.impl.presentation.article_list.mvi.ArticleListState
import dev.alexmester.impl.presentation.article_list.mvi.ArticleListViewModel
import dev.alexmester.ui.R
import dev.alexmester.ui.components.list_card.LaskArticleCard
import dev.alexmester.ui.components.notification_screen.LaskNotificationScreen
import dev.alexmester.ui.components.notification_screen.LayoutVariants
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
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
                        modifier = Modifier,
                        imageWarning = Icons.Default.Article,
                        textWarning = "No articles yet",
                        layoutVariants = LayoutVariants.WARNING,
                        showRetry = false
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