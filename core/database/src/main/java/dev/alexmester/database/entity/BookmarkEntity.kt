package dev.alexmester.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
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
    val bookmarkedAt: Long,
)
