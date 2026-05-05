package br.com.vibetube.app.data.repository

import br.com.vibetube.app.data.cache.dao.LikeDao
import br.com.vibetube.app.data.cache.entity.LikeEntity
import br.com.vibetube.app.domain.repository.LikesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Persiste curtidas localmente. Cada vídeo tem 0 ou 1 curtida (do próprio usuário no dispositivo).
 *
 * O contador `localLikeCount` reflete só o estado deste device. Quando migrarmos
 * para Cloud, sincronizaremos isLiked do servidor e o contador agregado vem da API.
 */
class LocalLikesRepository(
    private val likeDao: LikeDao
) : LikesRepository {

    override suspend fun isLiked(videoId: String): Boolean {
        return likeDao.get(videoId)?.isLiked == true
    }

    override fun observeLiked(videoId: String): Flow<Boolean> {
        return likeDao.observe(videoId).map { it?.isLiked == true }
    }

    override suspend fun toggle(videoId: String, postId: String?): Boolean {
        val current = likeDao.get(videoId)
        val newLiked = !(current?.isLiked ?: false)
        val newCount = (current?.localLikeCount ?: 0).let {
            if (newLiked) it + 1 else maxOf(0, it - 1)
        }
        likeDao.upsert(
            LikeEntity(
                videoId = videoId,
                postId = postId,
                isLiked = newLiked,
                localLikeCount = newCount,
                updatedAt = System.currentTimeMillis()
            )
        )
        return newLiked
    }
}
