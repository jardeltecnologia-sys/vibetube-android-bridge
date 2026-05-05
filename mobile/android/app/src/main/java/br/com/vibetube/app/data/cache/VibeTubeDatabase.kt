package br.com.vibetube.app.data.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.vibetube.app.data.cache.dao.CommentDao
import br.com.vibetube.app.data.cache.dao.IntroDao
import br.com.vibetube.app.data.cache.dao.LikeDao
import br.com.vibetube.app.data.cache.dao.SavedVideoDao
import br.com.vibetube.app.data.cache.dao.VideoDao
import br.com.vibetube.app.data.cache.entity.CommentEntity
import br.com.vibetube.app.data.cache.entity.IntroEntity
import br.com.vibetube.app.data.cache.entity.LikeEntity
import br.com.vibetube.app.data.cache.entity.SavedVideoEntity
import br.com.vibetube.app.data.cache.entity.VideoEntity

@Database(
    entities = [
        VideoEntity::class,
        CommentEntity::class,
        LikeEntity::class,
        SavedVideoEntity::class,
        IntroEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VibeTubeDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
    abstract fun commentDao(): CommentDao
    abstract fun likeDao(): LikeDao
    abstract fun savedVideoDao(): SavedVideoDao
    abstract fun introDao(): IntroDao

    companion object {
        fun create(context: Context): VibeTubeDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                VibeTubeDatabase::class.java,
                "vibetube.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
