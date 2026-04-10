package dev.alexmester.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Состояние пользователя для статьи: прочитано / аплодировано / в закладках.
 *
 * FK NO_ACTION — статья не удаляется автоматически пока есть активный user state.
 * Очистка статей происходит вручную через ArticleDao.deleteOrphaned().
 *
 * [clapCount] заменяет отдельную таблицу claps и isLiked флаг —
 * isLiked = clapCount > 0, нет риска рассинхронизации.
 */
@Entity(
    tableName = "article_user_state",
    foreignKeys = [
        ForeignKey(
            entity = ArticleEntity::class,
            parentColumns = ["id"],
            childColumns = ["articleId"],
            onDelete = ForeignKey.NO_ACTION,
        )
    ],
    indices = [
        Index("articleId"),
        Index("isBookmarked"),
        Index("isRead"),
        Index("clapCount"),
    ],
)
data class ArticleUserStateEntity(
    @PrimaryKey val articleId: Long,
    val isBookmarked: Boolean = false,
    val bookmarkedAt: Long? = null,
    val isRead: Boolean = false,
    val readAt: Long? = null,
    val clapCount: Int = 0,
)