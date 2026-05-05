package br.com.vibetube.app.domain.repository

import br.com.vibetube.app.domain.model.VibeVideo
import br.com.vibetube.app.domain.model.VibeResult
import kotlinx.coroutines.flow.Flow

interface VideoRepository {

    /**
     * Stream contínuo dos vídeos em cache.
     * Emite a cada mudança no Room.
     */
    fun observeVideos(): Flow<List<VibeVideo>>

    /**
     * Busca rede + atualiza cache. Retorna lista atualizada ou Failure.
     * Em caso de falha de rede, devolve Failure(cached=true) e a UI continua usando o stream.
     */
    suspend fun refresh(): VibeResult<List<VibeVideo>>

    /**
     * Recupera um vídeo específico por id.
     */
    suspend fun getById(id: String): VibeVideo?
}
