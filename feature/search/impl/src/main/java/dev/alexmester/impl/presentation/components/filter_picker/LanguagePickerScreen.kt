package dev.alexmester.impl.presentation.components.filter_picker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.components.FilterPickerTopBar
import dev.alexmester.models.locale.LocaleItem
import dev.alexmester.ui.R
import dev.alexmester.ui.components.locale.LaskLocaleRowItem
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.utils.locale.LocaleUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LanguagePickerScreen(
    selectedLanguage: String?,
    onSelect: (String?) -> Unit,
    onBack: () -> Unit,
) {
    val items = remember { LocaleUtils.buildLanguageItems() }

    Scaffold(
        topBar = {
            FilterPickerTopBar(
                title = stringResource(R.string.search_language),
                isApplyEnabled = false,
                onBack = onBack,
                onApply = {},
                showApply = false,
            )
        },
        containerColor = MaterialTheme.LaskColors.backgroundPrimary,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
            item {
                LaskLocaleRowItem(
                    isSelected = selectedLanguage == null,
                    item = LocaleItem("", "Any language", "🌐"),
                    onClick = { onSelect(null) },
                )
            }
            items(items, key = { it.code }) { item ->
                LaskLocaleRowItem(
                    isSelected = item.code == selectedLanguage,
                    item = item,
                    onClick = { onSelect(item.code) },
                )
            }
        }
    }
}