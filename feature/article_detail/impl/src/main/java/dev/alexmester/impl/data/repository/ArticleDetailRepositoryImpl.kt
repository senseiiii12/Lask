package dev.alexmester.impl.data.repository

import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.impl.data.local.ArticleDetailLocalDataSource
import dev.alexmester.impl.data.mappers.toDomain
import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.network.translate.TranslateApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ArticleDetailRepositoryImpl(
    private val local: ArticleDetailLocalDataSource,
    private val translateApiService: TranslateApiService,
    private val preferencesDataSource: UserPreferencesDataSource,
) : ArticleDetailRepository {

    override suspend fun getArticleById(id: Long): NewsArticle? =
        local.getArticleById(id)?.toDomain()

    override fun observeIsBookmarked(id: Long): Flow<Boolean> =
        local.observeIsBookmarked(id)

    override suspend fun isBookmarked(id: Long): Boolean =
        local.isBookmarked(id)

    override suspend fun toggleBookmark(articleId: Long): Boolean =
        local.toggleBookmark(articleId)

    override fun observeClapCount(id: Long): Flow<Int> =
        local.observeClapCount(id)

    override suspend fun getClapCount(id: Long): Int =
        local.getClapCount(id)

    override suspend fun addClap(articleId: Long) =
        local.addClap(articleId)

    override suspend fun markAsRead(articleId: Long) =
        local.markAsRead(articleId)

    override suspend fun translateText(
        text: String,
        targetLanguage: String,
        sourceLanguage: String?,
    ): String {
        val response = translateApiService.translate(
            text = text,
            targetLanguage = targetLanguage,
            sourceLanguage = sourceLanguage,
        )
        return response.data.translations.firstOrNull()?.translatedText
            ?: error("Empty translation response")
    }

    override suspend fun getAutoTranslateLanguage(): String? =
        preferencesDataSource.userPreferences.first().autoTranslateLanguage
}