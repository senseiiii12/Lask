package dev.alexmester.impl.presentation.article_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.article_list.mvi.ArticleListState
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
internal fun CategoryFilterRow(
    categories: List<Pair<String?, Int>>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = categories,
            key = { (cat, _) -> cat ?: "all" },
        ) { (category, count) ->
            val label = when (category) {
                null -> "All ($count)"
                ArticleListState.OTHER_CATEGORY -> "Other ($count)"
                else -> "${category.replaceFirstChar { it.uppercase() }} ($count)"
            }
            val isSelected = category == selectedCategory

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.LaskTypography.footnoteSemiBold,
                    )
                },
                shape = RoundedCornerShape(50.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.LaskColors.textPrimary,
                    selectedLabelColor = MaterialTheme.LaskColors.backgroundPrimary,
                    containerColor = MaterialTheme.LaskColors.backgroundSecondary,
                    labelColor = MaterialTheme.LaskColors.textPrimary,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = MaterialTheme.LaskColors.backgroundSecondary,
                    selectedBorderColor = MaterialTheme.LaskColors.textPrimary,
                ),
            )
        }
    }
}