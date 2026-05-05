package br.com.vibetube.app.data.cache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val blogId: String?,
    val authorName: String,
    val authorAvatarUrl: String?,
    val content: String,
    val publishedAt: String,
    val updatedAt: String?,
    val status: String,
    val source: String,
    val cachedAt: Long
)
