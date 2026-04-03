package dev.alexmester.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * История прочитанных статей.
 *
 * [articleId] — PrimaryKey, защита от дублей автоматически через REPLACE.
 * [readAt] — timestamp последнего прочтения, обновляется при повторном.
 * [articleTitle] — копируем при записи, чтобы экран "Read Articles"
 * работал даже после очистки кэша ленты.
 */
@Entity(tableName = "reading_history")
data class ReadingHistoryEntity(
    @PrimaryKey val articleId: Long,
    val articleTitle: String,
    val readAt: Long,
)