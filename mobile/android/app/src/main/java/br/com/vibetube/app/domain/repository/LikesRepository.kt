package br.com.vibetube.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface LikesRepository {
    suspend fun isLiked(videoId: String): Boolean
    fun observeLiked(videoId: String): Flow<Boolean>
    suspend fun toggle(videoId: String, postId: String?): Boolean
}
