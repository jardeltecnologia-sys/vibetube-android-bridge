package br.com.vibetube.app.data.cache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "likes")
data class LikeEntity(
    @PrimaryKey val videoId: String,
    val postId: String?,
    val isLiked: Boolean,
    val localLikeCount: Int,
    val updatedAt: Long
)
