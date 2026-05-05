package br.com.vibetube.app.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.vibetube.app.data.cache.entity.LikeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LikeDao {

    @Query("SELECT * FROM likes WHERE videoId = :videoId LIMIT 1")
    suspend fun get(videoId: String): LikeEntity?

    @Query("SELECT * FROM likes WHERE videoId = :videoId LIMIT 1")
    fun observe(videoId: String): Flow<LikeEntity?>

    @Query("SELECT * FROM likes WHERE isLiked = 1")
    fun observeAllLiked(): Flow<List<LikeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: LikeEntity)

    @Query("DELETE FROM likes WHERE videoId = :videoId")
    suspend fun delete(videoId: String)
}
