package br.com.vibetube.app.data.repository

import br.com.vibetube.app.core.config.FeatureFlagManager
import br.com.vibetube.app.data.blogger.BlogIntroDataSource
import br.com.vibetube.app.data.cache.dao.IntroDao
import br.com.vibetube.app.data.mapper.EntityMappers.toDomain
import br.com.vibetube.app.data.mapper.EntityMappers.toEntity
import br.com.vibetube.app.domain.model.VibeIntro
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Mantém a introdução dinâmica do blog em cache local.
 *
 * Estratégia:
 *   1. observeIntro() devolve cache (Room) — emite imediato
 *   2. refresh() busca HTML, extrai intro, persiste no Room
 *   3. Se rede falhar, mantém cache; se cache vazio, retorna fallback hardcoded
 */
class IntroRepository(
    private val dataSource: BlogIntroDataSource,
    private val introDao: IntroDao,
    private val flags: FeatureFlagManager
) {

    fun observeIntro(): Flow<VibeIntro?> {
        return introDao.observe().map { it?.toDomain() }
    }

    suspend fun getCached(): VibeIntro? = introDao.get()?.toDomain()

    suspend fun refresh(): Result<VibeIntro> {
        if (!flags.isEnabled(FeatureFlagManager.Flags.INTRO_FROM_BLOG)) {
            return Result.success(fallback())
        }
        val result = dataSource.fetchIntro()
        return if (result.isSuccess) {
            val intro = result.getOrThrow()
            introDao.upsert(intro.toEntity())
            Result.success(intro)
        } else {
            // Mantém cache; se vazio, devolve fallback
            val cached = introDao.get()?.toDomain()
            Result.success(cached ?: fallback())
        }
    }

    private fun fallback(): VibeIntro = VibeIntro(
        title = "VibeTube",
        subtitle = "Vídeos verticais, tendências e cultura digital.",
        body = "Feed vertical em tela cheia com gestos, áudio e leitura visual em estilo aplicativo nativo.",
        updatedAt = null,
        sourceUrl = flags.blogHomeUrl
    )
}
