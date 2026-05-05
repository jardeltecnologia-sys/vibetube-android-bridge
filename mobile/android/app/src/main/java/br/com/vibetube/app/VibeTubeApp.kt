package br.com.vibetube.app

import android.app.Application
import br.com.vibetube.app.core.config.FeatureFlagManager
import br.com.vibetube.app.data.cache.VibeTubeDatabase
import br.com.vibetube.app.data.blogger.BloggerFeedDataSource
import br.com.vibetube.app.data.blogger.BlogIntroDataSource
import br.com.vibetube.app.data.blogger.comments.BloggerCommentsDataSource
import br.com.vibetube.app.data.repository.BloggerVideoRepository
import br.com.vibetube.app.data.repository.LocalLikesRepository
import br.com.vibetube.app.data.repository.IntroRepository
import br.com.vibetube.app.data.repository.CommentsRepository
import br.com.vibetube.app.core.network.HttpClientProvider
import br.com.vibetube.app.core.share.ShareManager
import br.com.vibetube.app.core.utils.NetworkMonitor
import br.com.vibetube.app.core.config.UserPreferences
import br.com.vibetube.app.domain.usecase.ToggleLikeUseCase

/**
 * Service locator simples (sem Hilt para reduzir superfície de erros no primeiro build).
 * Tudo lazy, criado sob demanda.
 */
class VibeTubeApp : Application() {

    val featureFlags: FeatureFlagManager by lazy {
        FeatureFlagManager(this)
    }

    val database: VibeTubeDatabase by lazy {
        VibeTubeDatabase.create(this)
    }

    val httpClient by lazy { HttpClientProvider.create() }

    val feedDataSource by lazy {
        BloggerFeedDataSource(httpClient, featureFlags)
    }

    val introDataSource by lazy {
        BlogIntroDataSource(httpClient, featureFlags)
    }

    val commentsDataSource by lazy {
        BloggerCommentsDataSource(httpClient)
    }

    val videoRepository by lazy {
        BloggerVideoRepository(
            feedDataSource = feedDataSource,
            videoDao = database.videoDao(),
            savedVideoDao = database.savedVideoDao()
        )
    }

    val likesRepository by lazy {
        LocalLikesRepository(database.likeDao())
    }

    val introRepository by lazy {
        IntroRepository(introDataSource, database.introDao(), featureFlags)
    }

    val commentsRepository by lazy {
        CommentsRepository(commentsDataSource, database.commentDao())
    }

    val shareManager by lazy { ShareManager(featureFlags) }

    val networkMonitor by lazy { NetworkMonitor(this) }

    val userPreferences by lazy { UserPreferences(this) }

    val toggleLikeUseCase by lazy {
        ToggleLikeUseCase(likesRepository, featureFlags)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        @Volatile
        private var instance: VibeTubeApp? = null
        fun get(): VibeTubeApp = instance
            ?: throw IllegalStateException("VibeTubeApp not initialized")
    }
}
