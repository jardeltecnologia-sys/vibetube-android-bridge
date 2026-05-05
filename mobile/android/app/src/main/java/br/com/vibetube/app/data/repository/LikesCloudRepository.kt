package br.com.vibetube.app.data.repository

import br.com.vibetube.app.domain.repository.LikesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Stub para curtidas sincronizadas em nuvem.
 * Será ativado quando flag likesCloudSync=true e existir cloudApiBaseUrl configurada.
 */
class LikesCloudRepository : LikesRepository {

    override suspend fun isLiked(videoId: String): Boolean = false

    override fun observeLiked(videoId: String): Flow<Boolean> = flowOf(false)

    override suspend fun toggle(videoId: String, postId: String?): Boolean {
        // Quando ativado, fará POST /api/videos/{id}/like
        throw UnsupportedOperationException(
            "Sincronização de curtidas em nuvem ainda não está habilitada."
        )
    }
}
