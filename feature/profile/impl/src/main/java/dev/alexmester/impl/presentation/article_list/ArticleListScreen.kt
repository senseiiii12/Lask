package dev.alexmester.impl.presentation.article_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.api.navigation.ArticleListType
import dev.alexmester.impl.presentation.article_list.components.CategoryFilterRow
import dev.alexmester.impl.presentation.article_list.mvi.ArticleListIntent
import dev.alexmester.impl.presentation.article_list.mvi.ArticleListSideEffect
import dev.alexmester.impl.presentation.article_list.mvi.ArticleListState
import dev.alexmester.impl.presentation.article_list.mvi.ArticleListViewModel
import dev.alexmester.ui.components.list_card.LaskArticleCard
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
        ArticleListType.READ -> "Read Articles"
        ArticleListType.CLAPPED -> "Clapped Articles"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onIntent(ArticleListIntent.Back) }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardBackspace,
                            contentDescription = null,
                            tint = MaterialTheme.LaskColors.textPrimary,
                        )
                    }
                },
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.LaskTypography.h4,
                        color = MaterialTheme.LaskColors.textPrimary,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.LaskColors.backgroundPrimary,
                ),
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
                    Text(
                        text = "No articles yet",
                        style = MaterialTheme.LaskTypography.body1,
                        color = MaterialTheme.LaskColors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 32.dp),
                    )
                }

                else -> {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))

                        // ── Фильтры категорий ──────────────────────────────
                        CategoryFilterRow(
                            categories = state.categories,
                            selectedCategory = state.selectedCategory,
                            onCategorySelected = { category ->
                                onIntent(ArticleListIntent.SelectCategory(category))
                            },
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // ── Список статей ──────────────────────────────────
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 32.dp),
                        ) {
                            items(
                                items = state.filteredArticles,
                                key = { it.id },
                            ) { article ->
                                LaskArticleCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateItem(),
                                    article = article,
                                    isRead = true, // все статьи на этом экране прочитаны/аплодированы
                                    onClick = {
                                        onIntent(
                                            ArticleListIntent.ArticleClick(
                                                articleId = article.id,
                                                articleUrl = article.url,
                                            )
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}