package dev.alexmester.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_articles")
data class NewsArticleEntity(
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
    val cachedAt: Long,
    val sourceScreen: String,
    val clusterId: Int = -1,
){
    companion object {
        const val SOURCE_FEED = "FEED"
        const val SOURCE_SEARCH = "SEARCH"
    }
}