package dev.alexmester.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.alexmester.database.converter.StringListConverter
import dev.alexmester.database.dao.ArticleDao
import dev.alexmester.database.dao.ArticleUserStateDao
import dev.alexmester.database.dao.BookmarkDao
import dev.alexmester.database.dao.ClapDao
import dev.alexmester.database.dao.FeedCacheDao
import dev.alexmester.database.dao.NewsArticleDao
import dev.alexmester.database.dao.ReadingHistoryDao
import dev.alexmester.database.entity.ArticleEntity
import dev.alexmester.database.entity.ArticleUserStateEntity
import dev.alexmester.database.entity.BookmarkEntity
import dev.alexmester.database.entity.ClapEntity
import dev.alexmester.database.entity.FeedCacheEntity
import dev.alexmester.database.entity.NewsArticleEntity
import dev.alexmester.database.entity.ReadingHistoryEntity

@Database(
    entities = [
        ArticleEntity::class,
        ArticleUserStateEntity::class,
        FeedCacheEntity::class,
    ],
    version = 5,
    exportSchema = true,
)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun articleUserStateDao(): ArticleUserStateDao
    abstract fun feedCacheDao(): FeedCacheDao
}