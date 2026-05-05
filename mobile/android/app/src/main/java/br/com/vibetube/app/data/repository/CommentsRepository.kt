package br.com.vibetube.app.data.repository

import br.com.vibetube.app.data.blogger.comments.BloggerCommentsDataSource
import br.com.vibetube.app.data.cache.dao.CommentDao
import br.com.vibetube.app.data.mapper.EntityMappers.toDomain
import br.com.vibetube.app.data.mapper.EntityMappers.toEntity
import br.com.vibetube.app.domain.model.VibeComment
import br.com.vibetube.app.domain.model.VibeResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Lê comentários da Blogger API v3 e cacheia por postId.
 *
 * UI usa observeByPost(postId) para obter Flow do cache; chama refresh() ao abrir
 * a tela e ao puxar para atualizar.
 */
class CommentsRepository(
    private val dataSource: BloggerCommentsDataSource,
    private val commentDao: CommentDao
) {

    fun observeByPost(postId: String): Flow<List<VibeComment>> {
        return commentDao.observeByPost(postId).map { list -> list.map { it.toDomain() } }
    }

    suspend fun getCached(postId: String): List<VibeComment> {
        return commentDao.getByPost(postId).map { it.toDomain() }
    }

    suspend fun refresh(blogId: String?, postId: String): VibeResult<List<VibeComment>> {
        if (blogId.isNullOrBlank() || postId.isBlank()) {
            return VibeResult.Failure(
                IllegalArgumentException("blogId/postId ausentes — não é possível buscar comentários."),
                cached = false
            )
        }
        val result = dataSource.fetchComments(blogId = blogId, postId = postId)
        return if (result.isSuccess) {
            val comments = result.getOrThrow()
            // Substitui o cache desse post
            commentDao.deleteByPost(postId)
            commentDao.upsertAll(comments.map { it.toEntity() })
            VibeResult.Success(comments)
        } else {
            val hasCache = commentDao.countForPost(postId) > 0
            VibeResult.Failure(
                error = result.exceptionOrNull() ?: IllegalStateException("Erro ao buscar comentários"),
                cached = hasCache
            )
        }
    }
}
