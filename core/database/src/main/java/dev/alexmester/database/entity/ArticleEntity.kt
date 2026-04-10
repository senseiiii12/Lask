package dev.alexmester.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Единственный источник правды для данных статьи.
 *
 * Никогда не перезаписываем существующую запись через REPLACE —
 * только IGNORE, чтобы не уничтожить ArticleUserStateEntity через CASCADE.
 *
 * Удаляем статью только если:
 * - нет записи в feed_cache (не нужна ни одной ленте)
 * - нет активного user state (isBookmarked=false, isRead=false, clapCount=0)
 */
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val text: String?,
    val summary: String?,
    val url: String,
    val image: String?,
    val video: String?,
    val publishDate: String,
    val authors: String,
    val category: String?,
    val language: String?,
    val sourceCountry: String?,
    val sentiment: Double?,
)