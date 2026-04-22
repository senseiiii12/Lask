package dev.alexmester.impl.presentation.system

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.impl.presentation.system.components.SettingsValueRow
import dev.alexmester.impl.presentation.system.components.SystemTopBar
import dev.alexmester.impl.presentation.system.components.ThemeSelector
import dev.alexmester.impl.presentation.system.mvi.SystemIntent
import dev.alexmester.impl.presentation.system.mvi.SystemSideEffect
import dev.alexmester.impl.presentation.system.mvi.SystemState
import dev.alexmester.impl.presentation.system.mvi.SystemViewModel
import dev.alexmester.ui.R
import dev.alexmester.ui.components.buttons.LaskTextButton
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SystemScreen(
    onBack: () -> Unit,
    onNavigateToLocalePicker: (LocalePickerType) -> Unit,
    onNavigateToAutoTranslatePicker: () -> Unit,
    viewModel: SystemViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is SystemSideEffect.NavigateBack -> onBack()
                is SystemSideEffect.NavigateToLocalePicker ->
                    onNavigateToLocalePicker(effect.type)
                is SystemSideEffect.NavigateToAutoTranslatePicker ->
                    onNavigateToAutoTranslatePicker()
            }
        }
    }

    SystemScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SystemScreenContent(
    state: SystemState,
    onIntent: (SystemIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            SystemTopBar(
                modifier = Modifier,
                title = stringResource(R.string.profile_menu_system),
                onBack = { onIntent(SystemIntent.Back) }
            )
        },
        containerColor = MaterialTheme.LaskColors.backgroundPrimary,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.system_theme),
                    style = MaterialTheme.LaskTypography.body1,
                    color = MaterialTheme.LaskColors.textPrimary,
                    modifier = Modifier,
                )
                ThemeSelector(
                    selected = state.theme,
                    onSelect = { onIntent(SystemIntent.SetTheme(it)) },
                    modifier = Modifier.weight(1f),
                )
            }
            SettingsValueRow(
                label = stringResource(R.string.system_locale_language),
                value = state.languageDisplayName,
                onClick = { onIntent(SystemIntent.NavigateToLanguage) },
            )
            SettingsValueRow(
                label = stringResource(R.string.system_locale_country),
                value = state.countryDisplayName,
                onClick = { onIntent(SystemIntent.NavigateToCountry) },
            )
            SettingsValueRow(
                label = stringResource(R.string.system_locale_translate),
                value = state.autoTranslateDisplayName,
                onClick = { onIntent(SystemIntent.NavigateToAutoTranslateLanguage) },
            )
        }
    }
}
