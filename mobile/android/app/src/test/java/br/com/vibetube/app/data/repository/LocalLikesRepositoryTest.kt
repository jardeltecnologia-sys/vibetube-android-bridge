package br.com.vibetube.app.data.repository

import br.com.vibetube.app.data.cache.dao.LikeDao
import br.com.vibetube.app.data.cache.entity.LikeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalLikesRepositoryTest {

    /** Fake in-memory do LikeDao usando StateFlow de Map. */
    private class FakeLikeDao : LikeDao {
        private val state = MutableStateFlow<Map<String, LikeEntity>>(emptyMap())

        override suspend fun get(videoId: String): LikeEntity? = state.value[videoId]
        override fun observe(videoId: String): Flow<LikeEntity?> =
            state.map { it[videoId] }
        override fun observeAllLiked(): Flow<List<LikeEntity>> =
            state.map { it.values.filter { e -> e.isLiked } }
        override suspend fun upsert(item: LikeEntity) {
            state.value = state.value.toMutableMap().apply { put(item.videoId, item) }
        }
        override suspend fun delete(videoId: String) {
            state.value = state.value.toMutableMap().apply { remove(videoId) }
        }
    }

    @Test
    fun `toggle do nada salva curtida`() = runTest {
        val dao = FakeLikeDao()
        val repo = LocalLikesRepository(dao)
        val nowLiked = repo.toggle("v1", "p1")
        assertTrue(nowLiked)
        assertTrue(repo.isLiked("v1"))
        assertEquals(1, dao.get("v1")?.localLikeCount)
    }

    @Test
    fun `toggle duas vezes remove curtida`() = runTest {
        val dao = FakeLikeDao()
        val repo = LocalLikesRepository(dao)
        repo.toggle("v1", null)
        val state = repo.toggle("v1", null)
        assertFalse(state)
        assertFalse(repo.isLiked("v1"))
        assertEquals(0, dao.get("v1")?.localLikeCount)
    }

    @Test
    fun `isLiked retorna false sem registro`() = runTest {
        val dao = FakeLikeDao()
        val repo = LocalLikesRepository(dao)
        assertFalse(repo.isLiked("inexistente"))
    }

    @Test
    fun `localLikeCount nao fica negativo`() = runTest {
        val dao = FakeLikeDao()
        // Insere registro com isLiked=true e count=0 simulando estado inconsistente
        dao.upsert(LikeEntity("v1", null, isLiked = true, localLikeCount = 0, updatedAt = 0))
        val repo = LocalLikesRepository(dao)
        // Toggle agora vai para isLiked=false; count deveria ir pra max(0, 0-1) = 0
        val state = repo.toggle("v1", null)
        assertFalse(state)
        assertEquals(0, dao.get("v1")?.localLikeCount)
    }
}
