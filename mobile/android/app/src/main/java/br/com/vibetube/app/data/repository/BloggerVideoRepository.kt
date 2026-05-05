package br.com.vibetube.app.data.repository

import br.com.vibetube.app.data.blogger.BloggerFeedDataSource
import br.com.vibetube.app.data.cache.dao.SavedVideoDao
import br.com.vibetube.app.data.cache.dao.VideoDao
import br.com.vibetube.app.data.mapper.EntityMappers.toDomain
import br.com.vibetube.app.data.mapper.EntityMappers.toEntity
import br.com.vibetube.app.data.mapper.VideoMapper
import br.com.vibetube.app.domain.model.VibeResult
import br.com.vibetube.app.domain.model.VibeVideo
import br.com.vibetube.app.domain.repository.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * Implementação Bridge que busca do Blogger e cacheia em Room.
 *
 * Estratégia "cache-first":
 *   - observeVideos() retorna stream do Room (emite imediato)
 *   - refresh() busca rede e atualiza Room (Room re-emite)
 *   - se rede falhar e cache existir, devolve Failure(cached=true)
 */
class BloggerVideoRepository(
    private val feedDataSource: BloggerFeedDataSource,
    private val videoDao: VideoDao,
    private val savedVideoDao: SavedVideoDao,
    private val mapper: VideoMapper = VideoMapper()
) : VideoRepository {

    override fun observeVideos(): Flow<List<VibeVideo>> {
        return videoDao.observeAll()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.Default)
    }

    override suspend fun refresh(): VibeResult<List<VibeVideo>> {
        val result = feedDataSource.fetchFeed()
        return if (result.isSuccess) {
            val feed = result.getOrThrow()
            val videos = mapper.mapEntries(feed)
            // Persiste no Room
            videoDao.upsertAll(videos.map { it.toEntity() })
            VibeResult.Success(videos)
        } else {
            val cached = videoDao.count() > 0
            VibeResult.Failure(
                error = result.exceptionOrNull() ?: IllegalStateException("Erro desconhecido"),
                cached = cached
            )
        }
    }

    override suspend fun getById(id: String): VibeVideo? {
        return videoDao.getById(id)?.toDomain(
            isSaved = savedVideoDao.isSaved(id)
        )
    }
}
