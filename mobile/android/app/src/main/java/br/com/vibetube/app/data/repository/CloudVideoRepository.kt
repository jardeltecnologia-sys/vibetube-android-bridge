package br.com.vibetube.app.data.repository

import br.com.vibetube.app.domain.model.VibeResult
import br.com.vibetube.app.domain.model.VibeVideo
import br.com.vibetube.app.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Stub do repositório Cloud. Será ativado quando mode=="cloud" no config.
 * Por enquanto retorna lista vazia e Failure indicando que a Cloud está desativada.
 *
 * Quando ativarmos:
 *   - Trocar implementação para chamar CloudApiService
 *   - VibeTubeApp.videoRepository deverá selecionar este quando flags.isCloudMode()
 */
class CloudVideoRepository : VideoRepository {

    override fun observeVideos(): Flow<List<VibeVideo>> = flowOf(emptyList())

    override suspend fun refresh(): VibeResult<List<VibeVideo>> {
        return VibeResult.Failure(
            error = IllegalStateException(
                "Cloud mode desativado. Habilite cloudApiBaseUrl em vibetube_config.json."
            ),
            cached = false
        )
    }

    override suspend fun getById(id: String): VibeVideo? = null
}
