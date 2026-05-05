package br.com.vibetube.app.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.vibetube.app.data.cache.entity.CommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY publishedAt DESC")
    fun observeByPost(postId: String): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY publishedAt DESC")
    suspend fun getByPost(postId: String): List<CommentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CommentEntity>)

    @Query("DELETE FROM comments WHERE postId = :postId")
    suspend fun deleteByPost(postId: String)

    @Query("SELECT COUNT(*) FROM comments WHERE postId = :postId")
    suspend fun countForPost(postId: String): Int
}
