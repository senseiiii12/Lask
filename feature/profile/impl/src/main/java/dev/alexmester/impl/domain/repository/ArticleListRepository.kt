package dev.alexmester.impl.domain.repository

import dev.alexmester.models.news.NewsArticle
import kotlinx.coroutines.flow.Flow

interface ArticleListRepository {
    fun getReadArticles(): Flow<List<NewsArticle>>
    fun getClappedArticles(): Flow<List<NewsArticle>>
}