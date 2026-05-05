package br.com.vibetube.app.domain.usecase

import br.com.vibetube.app.core.config.FeatureFlagManager
import br.com.vibetube.app.domain.repository.LikesRepository

/**
 * Alterna curtida respeitando flags.
 * Quando likesCloudSync=true, deve usar LikesCloudRepository (injetar a partir do app).
 */
class ToggleLikeUseCase(
    private val likesRepository: LikesRepository,
    private val flags: FeatureFlagManager
) {

    sealed class Result {
        data class Liked(val showLocalNotice: Boolean) : Result()
        object Unliked : Result()
        object Disabled : Result()
    }

    suspend operator fun invoke(
        videoId: String,
        postId: String?,
        firstLikeOfSession: Boolean
    ): Result {
        if (!flags.isEnabled(FeatureFlagManager.Flags.LIKES_LOCAL) &&
            !flags.isEnabled(FeatureFlagManager.Flags.LIKES_CLOUD_SYNC)
        ) {
            return Result.Disabled
        }
        val isNowLiked = likesRepository.toggle(videoId, postId)
        return if (isNowLiked) {
            // Mostra aviso "salva neste dispositivo" só na primeira curtida da sessão
            // e somente se estamos no modo local (não cloud).
            val showNotice = firstLikeOfSession && !flags.isEnabled(FeatureFlagManager.Flags.LIKES_CLOUD_SYNC)
            Result.Liked(showLocalNotice = showNotice)
        } else {
            Result.Unliked
        }
    }
}
