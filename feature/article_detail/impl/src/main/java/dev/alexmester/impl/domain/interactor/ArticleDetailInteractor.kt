package dev.alexmester.impl.domain.interactor

import dev.alexmester.impl.domain.repository.ArticleDetailRepository
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.supervisorScope

class ArticleDetailInteractor(
    private val repository: ArticleDetailRepository,
) {
    suspend fun getArticle(id: Long): NewsArticle? =
        repository.getArticleById(id)

    fun observeIsBookmarked(id: Long): Flow<Boolean> =
        repository.observeIsBookmarked(id)

    suspend fun isBookmarked(id: Long): Boolean =
        repository.isBookmarked(id)

    suspend fun toggleBookmark(articleId: Long): Boolean =
        repository.toggleBookmark(articleId)

    fun observeClapCount(id: Long): Flow<Int> =
        repository.observeClapCount(id)

    suspend fun getClapCount(id: Long): Int =
        repository.getClapCount(id)

    suspend fun addClap(articleId: Long) =
        repository.addClap(articleId)

    suspend fun markAsRead(articleId: Long) =
        repository.markAsRead(articleId)

    suspend fun translateTexts(
        title: String,
        bodyText: String,
        targetLanguage: String,
        sourceLanguage: String?,
    ): AppResult<Pair<String, String>> = supervisorScope {
            val translatedTitle = async {
                repository.translateText(title, targetLanguage, sourceLanguage)
            }
            val translatedBody = async {
                repository.translateText(bodyText, targetLanguage, sourceLanguage)
            }
            val titleResult = translatedTitle.await()
            val bodyResult = translatedBody.await()

            when {
                titleResult is AppResult.Failure -> titleResult
                bodyResult is AppResult.Failure -> bodyResult
                titleResult is AppResult.Success && bodyResult is AppResult.Success -> {
                    AppResult.Success(titleResult.data to bodyResult.data)
                }
                else -> AppResult.Failure(NetworkError.Unknown())
            }
        }


    suspend fun getAutoTranslateLanguage(): String =
        repository.getAutoTranslateLanguage()
}