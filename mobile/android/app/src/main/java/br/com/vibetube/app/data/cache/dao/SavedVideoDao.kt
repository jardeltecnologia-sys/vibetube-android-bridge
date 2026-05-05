package br.com.vibetube.app.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.vibetube.app.data.cache.entity.SavedVideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedVideoDao {

    @Query("SELECT * FROM saved_videos ORDER BY savedAt DESC")
    fun observeAll(): Flow<List<SavedVideoEntity>>

    @Query("SELECT * FROM saved_videos WHERE videoId = :videoId LIMIT 1")
    suspend fun get(videoId: String): SavedVideoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: SavedVideoEntity)

    @Query("DELETE FROM saved_videos WHERE videoId = :videoId")
    suspend fun delete(videoId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_videos WHERE videoId = :videoId)")
    suspend fun isSaved(videoId: String): Boolean
}
