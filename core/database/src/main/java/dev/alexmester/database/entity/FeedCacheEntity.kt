package dev.alexmester.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Индекс ленты новостей — связывает статью с конкретной лентой и позицией.
 *
 * FK CASCADE — при удалении статьи из articles запись ленты тоже уходит.
 * Это нормально: лента это временный кэш, не пользовательские данные.
 *
 * [feedType] — тип ленты: FEED (топ новости), SEARCH (результаты поиска).
 * [clusterId] — индекс кластера внутри ленты для группировки /top-news.
 * [position] — позиция статьи внутри кластера для восстановления порядка.
 */
@Entity(
    tableName = "feed_cache",
    primaryKeys = ["feedType", "articleId"],
    foreignKeys = [
        ForeignKey(
            entity = ArticleEntity::class,
            parentColumns = ["id"],
            childColumns = ["articleId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index("articleId"),
        Index(value = ["feedType", "clusterId", "position"]),
    ],
)
data class FeedCacheEntity(
    val feedType: String,
    val articleId: Long,
    val clusterId: Int,
    val position: Int,
    val cachedAt: Long,
) {
    companion object {
        const val FEED_TOP = "FEED"
        const val FEED_SEARCH = "SEARCH"
    }
}