package dev.alexmester.impl.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.domain.model.SearchFilters
import dev.alexmester.impl.presentation.mvi.FilterType
import dev.alexmester.ui.components.buttons.LaskChipButton
import dev.alexmester.utils.locale.LocaleUtils
import dev.alexmester.utils.locale.LocaleUtils.countryCodeToFlagEmoji
import dev.alexmester.utils.locale.LocaleUtils.languageCodeToFlagEmoji

@Composable
internal fun SearchFilterRow(
    filters: SearchFilters,
    onFilterClick: (FilterType) -> Unit,
    onFilterDismiss: (FilterType) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            LaskChipButton(
                text = filters.category?.replaceFirstChar { it.uppercase() } ?: "Category",
                isSelected = filters.category != null,
                onClick = { onFilterClick(FilterType.CATEGORY) },
                onDismiss = { onFilterDismiss(FilterType.CATEGORY) },
            )
        }
        item {
            LaskChipButton(
                text = if (filters.country != null)
                    LocaleUtils.countryCodeToFullCountryName(filters.country)
                else "Country",
                leadingLocaleIcon = filters.country?.let { countryCodeToFlagEmoji(it) },
                isSelected = filters.country != null,
                onClick = { onFilterClick(FilterType.COUNTRY) },
                onDismiss = { onFilterDismiss(FilterType.COUNTRY) },
            )
        }
        item {
            LaskChipButton(
                text = if (filters.language != null)
                    LocaleUtils.languageCodeToFullLanguageName(filters.language)
                else "Language",
                leadingLocaleIcon = filters.language?.let { languageCodeToFlagEmoji(it) },
                isSelected = filters.language != null,
                onClick = { onFilterClick(FilterType.LANGUAGE) },
                onDismiss = { onFilterDismiss(FilterType.LANGUAGE) },
            )
        }
        item {
            val dateLabel = when {
                filters.earliestDate != null && filters.latestDate != null ->
                    "${filters.earliestDate.takeLast(5)} - ${filters.latestDate.takeLast(5)}"

                filters.earliestDate != null -> "From ${filters.earliestDate.takeLast(5)}"
                filters.latestDate != null -> "To ${filters.latestDate.takeLast(5)}"
                else -> "Date period"
            }
            LaskChipButton(
                text = dateLabel,
                isSelected = filters.earliestDate != null || filters.latestDate != null,
                onClick = { onFilterClick(FilterType.DATE) },
                onDismiss = { onFilterDismiss(FilterType.DATE) },
            )
        }
        item {
            val sortLabel = if (filters.sortAscending) "Oldest first" else "Newest first"
            LaskChipButton(
                text = sortLabel,
                isSelected = filters.sortAscending,
                onClick = { onFilterClick(FilterType.SORT) },
                onDismiss = { onFilterDismiss(FilterType.SORT) },
            )
        }
    }
}