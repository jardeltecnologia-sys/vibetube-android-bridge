package br.com.vibetube.app.data.cache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val blogId: String?,
    val postId: String?,
    val source: String,
    val title: String,
    val intro: String,
    val description: String,
    val videoUrl: String?,
    val embedUrl: String?,
    val thumbnailUrl: String?,
    val postUrl: String,
    val commentsUrl: String?,
    val authorName: String,
    val publishedAt: String,
    val updatedAt: String,
    val labelsJson: String,
    val commentCount: Int,
    val cachedAt: Long
)
