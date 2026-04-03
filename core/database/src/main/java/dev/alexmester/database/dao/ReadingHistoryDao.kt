package dev.alexmester.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.alexmester.database.entity.ReadingHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: ReadingHistoryEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM reading_history WHERE articleId = :articleId)")
    fun isRead(articleId: Long): Flow<Boolean>

    // Для визуальной метки в ленте — возвращаем Set id всех прочитанных
    @Query("SELECT articleId FROM reading_history")
    fun getReadArticleIds(): Flow<List<Long>>

    // Для счётчика на профиле
    @Query("SELECT COUNT(*) FROM reading_history")
    fun getReadCount(): Flow<Int>

    // Для экрана "Read Articles"
    @Query("SELECT * FROM reading_history ORDER BY readAt DESC")
    fun getAllHistory(): Flow<List<ReadingHistoryEntity>>
}