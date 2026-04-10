package dev.alexmester.impl.presentation.article_list.mvi

import dev.alexmester.models.news.NewsArticle

data class ArticleListState(
    val allArticles: List<NewsArticle> = emptyList(),
    val selectedCategory: String? = null, // null = All
    val isLoading: Boolean = true,
) {
    /**
     * Категории вычисляются из статей.
     * Статьи без категории попадают в "Other".
     */
    val categories: List<Pair<String?, Int>>
        get() {
            val grouped = allArticles.groupBy { it.category ?: OTHER_CATEGORY }
            val result = mutableListOf<Pair<String?, Int>>()
            // "All" первым
            result.add(null to allArticles.size)
            grouped.entries
                .sortedByDescending { it.value.size }
                .forEach { (category, articles) ->
                    result.add(category to articles.size)
                }
            return result
        }

    val filteredArticles: List<NewsArticle>
        get() = when (selectedCategory) {
            null -> allArticles
            OTHER_CATEGORY -> allArticles.filter { it.category == null }
            else -> allArticles.filter { it.category == selectedCategory }
        }

    companion object {
        const val OTHER_CATEGORY = "Other"
    }
}