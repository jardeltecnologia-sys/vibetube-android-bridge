package br.com.vibetube.app.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.vibetube.app.data.cache.entity.VideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {

    @Query("SELECT * FROM videos ORDER BY publishedAt DESC")
    fun observeAll(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos ORDER BY publishedAt DESC")
    suspend fun getAll(): List<VideoEntity>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): VideoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<VideoEntity>)

    @Query("UPDATE videos SET commentCount = :count WHERE id = :id")
    suspend fun updateCommentCount(id: String, count: Int)

    @Query("DELETE FROM videos WHERE cachedAt < :olderThan")
    suspend fun pruneOlderThan(olderThan: Long)

    @Query("SELECT COUNT(*) FROM videos")
    suspend fun count(): Int
}
