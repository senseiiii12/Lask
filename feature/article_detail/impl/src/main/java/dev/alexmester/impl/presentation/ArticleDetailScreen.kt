package dev.alexmester.impl.presentation

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.impl.presentation.components.ArticleDetailBottomBar
import dev.alexmester.impl.presentation.components.ArticleDetailContent
import dev.alexmester.ui.components.snackbar.LaskTopSnackbarHost
import dev.alexmester.ui.components.snackbar.showLaskSnackbar
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ArticleDetailScreen(
    articleId: Long,
    articleUrl: String,
    onBack: () -> Unit,
    viewModel: ArticleDetailViewModel = koinViewModel(
        parameters = { parametersOf(articleId, articleUrl) }
    ),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                ArticleDetailSideEffect.NavigateBack -> onBack()
                is ArticleDetailSideEffect.ShowSnackbar ->
                    snackbarHostState.showLaskSnackbar(effect.message)

                is ArticleDetailSideEffect.ShareUrl -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, effect.url)
                    }
                    context.startActivity(Intent.createChooser(intent, "Поделиться"))
                }
            }
        }
    }

    ArticleDetailScreen(
        state = state,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState
    )
}

@Composable
internal fun ArticleDetailScreen(
    modifier: Modifier = Modifier,
    state: ArticleDetailState,
    onIntent: (ArticleDetailIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        bottomBar = {
            ArticleDetailBottomBar(
                state = state.contentOrNull ,
                onIntent = onIntent,
            )
        }
    ) { paddingValues ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = state) {
                ArticleDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.LaskColors.brand_blue,
                    )
                }

                is ArticleDetailState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.LaskTypography.body1,
                        color = MaterialTheme.LaskColors.error,
                    )
                }

                is ArticleDetailState.Content -> {
                    ArticleDetailContent(
                        state = state,
                        onIntent = onIntent,
                    )
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