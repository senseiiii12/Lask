package dev.alexmester.impl.data.repository

import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.impl.data.local.ArticleDetailLocalDataSource
import dev.alexmester.impl.data.mappers.toDomain
import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import dev.alexmester.network.ext.safeApiCall
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

    override suspend fun addClap(articleId: Long){
        local.addClap(articleId)
        preferencesDataSource.addXp(XP_PER_CLAP)
    }

    override suspend fun markAsRead(articleId: Long) {
        local.markAsRead(articleId)
        preferencesDataSource.addXp(XP_PER_READ)
    }

    override suspend fun translateText(
        text: String,
        targetLanguage: String,
        sourceLanguage: String?,
    ): AppResult<String> = safeApiCall {
        val truncated = if (text.length > MAX_TRANSLATE_CHARS) {
            text.take(MAX_TRANSLATE_CHARS)
                .substringBeforeLast(
                    delimiter = '.',
                    missingDelimiterValue = text.take(MAX_TRANSLATE_CHARS)
                ) + "..."
        } else text
        val response = translateApiService.translate(
            text = truncated ,
            targetLanguage = targetLanguage,
            sourceLanguage = sourceLanguage,
        )
        response.translations.translatedText
    }

    override suspend fun getAutoTranslateLanguage(): String =
        preferencesDataSource.userPreferences.first().autoTranslateLanguage

    private companion object {
        private const val MAX_TRANSLATE_CHARS = 5_000
        private const val XP_PER_READ = 25f
        private const val XP_PER_CLAP = 10f
    }
}