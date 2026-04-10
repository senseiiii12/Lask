package dev.alexmester.impl.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.api.navigation.ArticleListType
import dev.alexmester.impl.presentation.profile.components.ProfileContent
import dev.alexmester.impl.presentation.profile.mvi.ProfileIntent
import dev.alexmester.impl.presentation.profile.mvi.ProfileSideEffect
import dev.alexmester.impl.presentation.profile.mvi.ProfileState
import dev.alexmester.impl.presentation.profile.mvi.ProfileViewModel
import dev.alexmester.ui.desing_system.LaskColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    onNavigateToArticleList: (ArticleListType) -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is ProfileSideEffect.NavigateToArticleList ->
                    onNavigateToArticleList(effect.type)
            }
        }
    }

    ProfileScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileScreenContent(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.LaskColors.backgroundPrimary,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(paddingValues)
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            ProfileContent(
                modifier = Modifier,
                profileState = state,
                onIntent = { onIntent(it) }
            )
        }
    }
}