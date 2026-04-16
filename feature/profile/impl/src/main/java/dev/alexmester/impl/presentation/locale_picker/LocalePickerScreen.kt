package dev.alexmester.impl.presentation.locale_picker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.impl.presentation.locale_picker.components.LocalePickerTopBar
import dev.alexmester.impl.presentation.locale_picker.mvi.*
import dev.alexmester.ui.components.locale.LaskLocaleRowItem
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import dev.alexmester.utils.CompatibilityWarning
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.Locale

@Composable
fun LocalePickerScreen(
    type: LocalePickerType,
    onBack: () -> Unit,
    onCountryOverride: (String) -> Unit = {},
    onLanguageOverride: (String) -> Unit = {},
    viewModel: LocalePickerViewModel = koinViewModel(
        parameters = { parametersOf(type) }
    ),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                LocalePickerSideEffect.NavigateBack -> onBack()
                is LocalePickerSideEffect.ApplyWithCountryOverride -> {
                    onCountryOverride(effect.country)
//                    onBack()
                }
                is LocalePickerSideEffect.ApplyWithLanguageOverride -> {
                    onLanguageOverride(effect.language)
//                    onBack()
                }
            }
        }
    }

    LocalePickerScreenContent(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LocalePickerScreenContent(
    state: LocalePickerState,
    onIntent: (LocalePickerIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            LocalePickerTopBar(
                title = state.title.asString(),
                isApplyEnabled = state.isApplyEnabled,
                onBack = { onIntent(LocalePickerIntent.Back) },
                onApply = { onIntent(LocalePickerIntent.Apply) },
            )
        },
        containerColor = MaterialTheme.LaskColors.backgroundPrimary,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(paddingValues),
        ) {
            AnimatedVisibility(
                visible = state.compatibilityWarning != null,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                state.compatibilityWarning?.let { warning ->
                    CompatibilityWarningBanner(
                        type = state.type,
                        warning = warning,
                        onAdaptSelf = { onIntent(LocalePickerIntent.ResolveCompatibility(adaptSelf = true)) },
                        onAdaptOther = { onIntent(LocalePickerIntent.ResolveCompatibility(adaptSelf = false)) },
                        onDismiss = { onIntent(LocalePickerIntent.DismissCompatibilityWarning) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                items(items = state.items, key = { it.code }) { item ->
                    LaskLocaleRowItem(
                        isSelected = item.code == state.pendingCode,
                        item = item,
                        onClick = { onIntent(LocalePickerIntent.SelectItem(item.code)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CompatibilityWarningBanner(
    type: LocalePickerType,
    warning: CompatibilityWarning,
    onAdaptSelf: () -> Unit,
    onAdaptOther: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val suggestedCountryName = Locale("", warning.suggestedCountry.uppercase()).displayCountry
    val suggestedLanguageName = Locale(warning.suggestedLanguage).getDisplayLanguage(Locale.ENGLISH)
        .replaceFirstChar { it.uppercase() }

    // Тексты кнопок зависят от того, что выбирали
    val selfLabel: String
    val otherLabel: String
    when (type) {
        LocalePickerType.LANGUAGE -> {
            selfLabel = "Сменить язык → $suggestedLanguageName"
            otherLabel = "Сменить страну → $suggestedCountryName"
        }
        LocalePickerType.COUNTRY -> {
            selfLabel = "Сменить страну → $suggestedCountryName"
            otherLabel = "Сменить язык → $suggestedLanguageName"
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.LaskColors.warning.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.LaskColors.warning,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Возможен пустой результат",
                    style = MaterialTheme.LaskTypography.body2SemiBold,
                    color = MaterialTheme.LaskColors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.LaskColors.textSecondary,
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Выбранный язык и страна обычно не используются вместе. API может вернуть пустую ленту.",
                style = MaterialTheme.LaskTypography.footnote,
                color = MaterialTheme.LaskColors.textSecondary,
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onAdaptSelf,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = selfLabel,
                        style = MaterialTheme.LaskTypography.footnote,
                        maxLines = 2,
                    )
                }
                OutlinedButton(
                    onClick = onAdaptOther,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = otherLabel,
                        style = MaterialTheme.LaskTypography.footnote,
                        maxLines = 2,
                    )
                }
            }
        }
    }
}