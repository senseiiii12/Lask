package dev.alexmester.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

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
        const val TRENDS_FEED = "TRENDS_FEED"
        const val EXPLORE_FEED = "EXPLORE_FEED"
    }
}