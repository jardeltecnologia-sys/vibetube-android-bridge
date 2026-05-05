package br.com.vibetube.app.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.vibetube.app.data.cache.entity.IntroEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IntroDao {

    @Query("SELECT * FROM intro_cache WHERE id = 'current' LIMIT 1")
    suspend fun get(): IntroEntity?

    @Query("SELECT * FROM intro_cache WHERE id = 'current' LIMIT 1")
    fun observe(): Flow<IntroEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: IntroEntity)
}
