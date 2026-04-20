package dev.alexmester.impl.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.impl.presentation.components.FilterOverlay
import dev.alexmester.impl.presentation.components.SearchFilterRow
import dev.alexmester.impl.presentation.mvi.FilterType
import dev.alexmester.impl.presentation.mvi.SearchIntent
import dev.alexmester.impl.presentation.mvi.SearchSideEffect
import dev.alexmester.impl.presentation.mvi.SearchState
import dev.alexmester.impl.presentation.mvi.SearchViewModel
import dev.alexmester.ui.R
import dev.alexmester.ui.components.buttons.LaskTextButton
import dev.alexmester.ui.components.input_field.LaskTextField
import dev.alexmester.ui.components.list_card.LaskArticleCard
import dev.alexmester.ui.components.notification_screen.LaskNotificationScreen
import dev.alexmester.ui.components.notification_screen.NotificationType
import dev.alexmester.ui.desing_system.LaskColors
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    onCancel: () -> Unit,
    onArticleClick: (Long, String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val readArticleIds by viewModel.readArticleIds.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                SearchSideEffect.NavigateBack -> onCancel()
                is SearchSideEffect.NavigateToArticle ->
                    onArticleClick(effect.articleId, effect.articleUrl)
            }
        }
    }

    SearchScreenContent(
        state = state,
        readArticleIds = readArticleIds,
        onIntent = viewModel::handleIntent,
    )
}

@Composable
internal fun SearchScreenContent(
    state: SearchState,
    readArticleIds: Set<Long>,
    onIntent: (SearchIntent) -> Unit,
) {
    var activeFilter by remember { mutableStateOf<FilterType?>(null) }
    val keyboard = LocalSoftwareKeyboardController.current
    val focus = LocalFocusManager.current

    if (activeFilter != null) {
        FilterOverlay(
            filterType = activeFilter!!,
            filters = state.filters,
            onFiltersChanged = { newFilters ->
                onIntent(SearchIntent.FiltersChanged(newFilters))
                activeFilter = null
            },
            onBack = { activeFilter = null },
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.LaskColors.backgroundPrimary)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LaskTextField(
                modifier = Modifier.weight(1f),
                text = state.query,
                placeholderText = stringResource(R.string.search_placeholder),
                leadingIcon = Icons.Outlined.Search,
                onValueChange = { onIntent(SearchIntent.QueryChanged(it)) },
                onClearClick = { onIntent(SearchIntent.ClearQuery) },
                onDone = {
                    keyboard?.hide()
                    focus.clearFocus()
                    onIntent(SearchIntent.Search)
                },
            )
            LaskTextButton(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(R.string.search_back),
                textColor = MaterialTheme.LaskColors.brand_blue,
                onClick = { onIntent(SearchIntent.Cancel) },
            )
        }

        SearchFilterRow(
            filters = state.filters,
            onFilterClick = { filterType ->
                keyboard?.hide()
                focus.clearFocus()
                activeFilter = filterType
            },
            onFilterDismiss = { filterType ->
                val cleared = when (filterType) {
                    FilterType.CATEGORY -> state.filters.copy(category = null)
                    FilterType.COUNTRY -> state.filters.copy(country = null)
                    FilterType.LANGUAGE -> state.filters.copy(language = null)
                    FilterType.DATE -> state.filters.copy(earliestDate = null, latestDate = null)
                    FilterType.SORT -> state.filters.copy(sortAscending = false)
                }
                onIntent(SearchIntent.FiltersChanged(cleared))
            },
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.LaskColors.brand_blue,
                        trackColor = MaterialTheme.LaskColors.brand_blue10,
                    )
                }

                state.error != null -> {
                    LaskNotificationScreen(
                        type = NotificationType.Error(state.error),
                    )
                }

                state.hasSearched && state.results.isEmpty() -> {
                    LaskNotificationScreen(
                        type = NotificationType.Warning(
                            text = stringResource(R.string.warning_no_resutl_for_query,"\"${state.query}\""),
                            image = Icons.Default.SearchOff
                        ),
                    )
                }

                state.results.isNotEmpty() -> {
                    val listState = rememberLazyListState()
                    LaunchedEffect(listState) {
                        snapshotFlow {
                            val layoutInfo = listState.layoutInfo
                            val totalItems = layoutInfo.totalItemsCount
                            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                            lastVisible >= totalItems - 4
                        }
                            .distinctUntilChanged()
                            .filter { it }
                            .collect { onIntent(SearchIntent.LoadMore) }
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(state.results, key = { "${it.id}-${it.publishDate}" }) { article ->
                            LaskArticleCard(
                                modifier = Modifier.fillMaxWidth(),
                                article = article,
                                isRead = article.id in readArticleIds,
                                onClick = {
                                    onIntent(SearchIntent.ArticleClick(article.id, article.url))
                                },
                            )
                        }

                        if (state.isLoadingMore) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.LaskColors.brand_blue,
                                        trackColor = MaterialTheme.LaskColors.brand_blue10,
                                    )
                                }
                            }
                        }
                    }
                }

                !state.hasSearched && state.query.isEmpty() -> {
                    LaskNotificationScreen(
                        type = NotificationType.Warning(
                            text = stringResource(R.string.warning_search_news),
                            image = Icons.Default.Search
                        ),
                    )
                }
            }
        }
    }
}

